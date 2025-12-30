// Embedded HTTP server that renders the .txt test files (live) and serves assets.
//which will alow us to handle adding/removing in real time.
package com.compiler.flask.server;

import com.compiler.flask.ast.ProgramNode;
import com.compiler.flask.core.RenderContext;
import com.compiler.flask.core.ScriptContextExtractor;
import com.compiler.flask.emitter.HtmlEmitterVisitor;
import com.compiler.flask.frontend.FlaskParserFacade;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.*;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public final class HttpAppServer {

    private final int port;

    private final Map<String, ProgramNode> pages = new HashMap<>();

    private final Path assetsDir;

    private List<Object> sharedProducts;

    public HttpAppServer(int port, Path projectRoot) {
        this.port = port;
        this.assetsDir = projectRoot.resolve("src/main/resources/assets");
    }

    public void start() throws IOException {

        loadPages();


        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/products", exchange -> handleRender(exchange, "show_products"));
        server.createContext("/products/add", exchange -> handleRender(exchange, "add_product"));
        server.createContext("/products/detail", exchange -> handleRender(exchange, "show_product_details"));

        server.createContext("/api/products/add", this::handleAddProduct);
        server.createContext("/api/products/delete", this::handleDeleteProduct);
        server.createContext("/assets", this::handleAsset);

        server.start();
        System.out.printf("Server running on http://localhost:%d%n", port);
    }

    private void loadPages() throws IOException {

        ProgramNode showProducts = loadPage(Path.of("src/main/resources/tests/show_products.txt"));
        pages.put("show_products", showProducts);
        pages.put("add_product", loadPage(Path.of("src/main/resources/tests/add_product.txt")));
        pages.put("show_product_details", loadPage(Path.of("src/main/resources/tests/show_product_details.txt")));


        sharedProducts = extractProducts(showProducts);
    }

    private ProgramNode loadPage(Path scriptPath) throws IOException {
        String source = Files.readString(scriptPath);
        FlaskParserFacade parser = new FlaskParserFacade();
        return parser.parse(source, scriptPath.toString());
    }

    private List<Object> extractProducts(ProgramNode program) {

        ScriptContextExtractor extractor = new ScriptContextExtractor();
        RenderContext base = extractor.extract(program);
        Object products = base.getVariable("products");
        if (products instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<Object> raw = (List<Object>) list;
            return raw;
        }
        return null;
    }

    private void handleRender(HttpExchange exchange, String key) throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendPlain(exchange, 405, "Method Not Allowed");
            return;
        }
        ProgramNode program = pages.get(key);
        if (program == null) {
            sendPlain(exchange, 404, "Not found");
            return;
        }

        RenderContext ctx = new RenderContext();
        if ("show_product_details".equals(key)) {

            String name = queryParam(exchange.getRequestURI());
            Map<String, Object> product = findProductByName(name);
            if (product == null) {
                sendPlain(exchange, 404, "Product not found");
                return;
            }
            ctx.setVariable("product", product);
        } else if ("show_products".equals(key) && sharedProducts != null) {

            ctx.setVariable("products", sharedProducts);
        }

        String html = new HtmlEmitterVisitor(ctx).emit(program);
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleAddProduct(HttpExchange exchange) throws IOException {

         if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendPlain(exchange, 405, "Method Not Allowed");
            return;
        }
        if (sharedProducts == null) {
            sendPlain(exchange, 500, "Products data not loaded");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> product = parseParams(body);
        if (!product.isEmpty()) {

            sharedProducts.add(product);
            redirect(exchange);
        } else {
            sendPlain(exchange, 400, "Expected form data");
        }
    }

    private void handleDeleteProduct(HttpExchange exchange) throws IOException {

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendPlain(exchange, 405, "Method Not Allowed");
            return;
        }
        if (sharedProducts == null) {
            sendPlain(exchange, 500, "Products data not loaded");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String name = parseDeleteName(body);
        if (name == null || name.isBlank()) {
            sendPlain(exchange, 400, "Provide a product name to remove");
            return;
        }

        sharedProducts.removeIf(item -> matchesName(item, name));
        redirect(exchange);
    }

    private void handleAsset(HttpExchange exchange) throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendPlain(exchange, 405, "Method Not Allowed");
            return;
        }
        URI uri = exchange.getRequestURI();
        String path = uri.getPath().replaceFirst("^/assets/?", "");
        Path file = assetsDir.resolve(path);
        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            sendPlain(exchange, 404, "Asset not found");
            return;
        }
        byte[] bytes = Files.readAllBytes(file);
        String contentType = guessContentType(file);
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String guessContentType(Path file) {

        String name = file.getFileName().toString().toLowerCase();
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    private void redirect(HttpExchange exchange) throws IOException {

        Headers headers = exchange.getResponseHeaders();
        headers.add("Location", "/products");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void sendPlain(HttpExchange exchange, int status, String message) throws IOException {

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String queryParam(URI uri) {

        Map<String, String> params = parseParams(uri.getRawQuery());
        return params.get("name");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> findProductByName(String name) {

        if (name == null || sharedProducts == null) {
            return null;
        }
        for (Object item : sharedProducts) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            Object n = map.get("name");
            if (n != null && name.equalsIgnoreCase(n.toString())) {
                return (Map<String, Object>) map;
            }
        }
        return null;
    }

    private String parseDeleteName(String body) {

        Map<String, String> form = parseParams(body);
        return form.get("name");
    }

    private Map<String, String> parseParams(String body) {

        Map<String, String> result = new LinkedHashMap<>();
        if (body == null || body.isBlank()) {
            return result;
        }
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx < 0) {
                continue;
            }
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String val = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            result.put(key, val);
        }
        return result;
    }

    private boolean matchesName(Object item, String name) {

        if (item instanceof Map<?, ?> map) {
            Object value = map.get("name");
            return value != null && name.equals(value.toString());
        }
        return name.equals(item != null ? item.toString() : null);
    }
}
