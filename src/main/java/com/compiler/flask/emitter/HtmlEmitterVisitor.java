package com.compiler.flask.emitter;

import com.compiler.flask.ast.*;
import com.compiler.flask.core.RenderContext;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HtmlEmitterVisitor {
    private static final Pattern EXPR_PATTERN = Pattern.compile("\\{\\{\\s*(.*?)\\s*}}");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");

    private final RenderContext context;

    private final Deque<Map<String, Object>> frames = new ArrayDeque<>();

    private final StringBuilder bodyBuilder = new StringBuilder();

    private final List<String> cssBlocks = new ArrayList<>();

    public HtmlEmitterVisitor(RenderContext context) {
        this.context = context;
    }

    public String emit(ProgramNode program) {
        reset();


        for (AstNode child : program.getChildren()) {
            if (child instanceof RouteNode route) {
                renderRoute(route);
                break;
            }
        }
        return buildDocument();
    }

    private void reset() {
        bodyBuilder.setLength(0);
        cssBlocks.clear();
        frames.clear();
        frames.push(context.globals());
    }

    private void renderRoute(RouteNode route) {
        appendLine("<!-- Route " + route.getPath() + " -->");

        for (AstNode child : route.getChildren()) {
            if (child instanceof TemplateNode template) {
                renderNodes(template.getChildren());
                break;
            }
        }
    }

    private void renderNodes(List<AstNode> nodes) {
        for (int i = 0; i < nodes.size();) {
            AstNode node = nodes.get(i);
            if (node instanceof CssBlockNode css) {

                cssBlocks.add(css.toCssText());
                i++;
                continue;
            }
            if (node instanceof JinjaNode jinja) {
                if (jinja.getKind() == JinjaNode.Kind.STMT) {
                    String stmt = jinja.getBody().trim();
                    if (stmt.startsWith("for ")) {

                        int endIndex = findMatchingEnd(nodes, i + 1);
                        List<AstNode> block = new ArrayList<>(nodes.subList(i + 1, endIndex));
                        executeLoop(stmt, block);
                        i = endIndex + 1;
                        continue;
                    }
                    if (stmt.startsWith("endfor")) {
                        i++;
                        continue;
                    }
                    i++;
                    continue;
                } else {
                    appendLine(resolveExpression(jinja.getBody()));
                    i++;
                    continue;
                }
            }

            if (node instanceof HtmlLineNode html) {
                appendLine(substituteExpressions(html.getContent()));
            } else {

                renderNodes(node.getChildren());
            }
            i++;
        }
    }

    private void executeLoop(String stmt, List<AstNode> block) {

        String body = stmt.substring(3).trim();
        String[] parts = body.split("\\s+in\\s+");
        if (parts.length != 2) {
            throw new IllegalStateException("Unsupported loop syntax: " + stmt);
        }
        String variable = parts[0].trim();
        String iterableExpr = parts[1].trim();

        Object iterable = resolveValue(iterableExpr);
        if (!(iterable instanceof Iterable<?> items)) {
            return;
        }
        for (Object item : items) {
            pushFrame(variable, item);
            renderNodes(block);
            popFrame();
        }
    }

    private int findMatchingEnd(List<AstNode> nodes, int start) {

        for (int i = start; i < nodes.size(); i++) {
            AstNode candidate = nodes.get(i);
            if (candidate instanceof JinjaNode j && j.getKind() == JinjaNode.Kind.STMT) {
                String stmt = j.getBody().trim();
                if (stmt.startsWith("for ")) {
                    throw new IllegalStateException("Nested for-loops are not supported");
                }
                if (stmt.startsWith("endfor")) {
                    return i;
                }
            }
        }
        throw new IllegalStateException("Missing terminator endfor");
    }

    private String substituteExpressions(String text) {

        Matcher matcher = EXPR_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String expr = matcher.group(1);
            String resolved = resolveExpression(expr);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(resolved));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String resolveExpression(String expr) {
        Object value = resolveValue(expr.trim());
        if (value == null) {
            return "{{ " + expr.trim() + " }}";
        }
        if (value instanceof Number number) {
            double dbl = number.doubleValue();
            if (dbl == Math.rint(dbl)) {
                return Long.toString((long) dbl);
            }
            return NUMBER_FORMAT.format(dbl);
        }
        return value.toString();
    }

    private Object resolveValue(String expr) {
        String[] parts = expr.split("\\.");
        Object current = lookup(parts[0]);
        for (int i = 1; i < parts.length && current != null; i++) {
            if (current instanceof Map<?, ?> map) {
                current = map.get(parts[i]);
            } else {
                return null;
            }
        }
        return current;
    }

    private Object lookup(String name) {
        for (Map<String, Object> frame : frames) {
            if (frame.containsKey(name)) {
                return frame.get(name);
            }
        }
        return null;
    }

    private void pushFrame(String variable, Object value) {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put(variable, value);
        frames.push(frame);
    }

    private void popFrame() {
        if (!frames.isEmpty()) {
            frames.pop();
        }
    }

    private void appendLine(String text) {
        bodyBuilder.append(text).append(System.lineSeparator());
    }

    private String buildDocument() {

        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"utf-8\"/>\n<title>FlaskCompiler Preview</title>\n");
        if (!cssBlocks.isEmpty()) {
            builder.append("<style>\n");
            for (String block : cssBlocks) {
                builder.append(block).append(System.lineSeparator());
            }
            builder.append("</style>\n");
        }
        builder.append("</head>\n<body>\n");
        builder.append(bodyBuilder);
        builder.append("</body>\n</html>\n");
        return builder.toString();
    }

}
