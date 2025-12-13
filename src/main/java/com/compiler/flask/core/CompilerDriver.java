package com.compiler.flask.core;

import com.compiler.flask.ast.ProgramNode;
import com.compiler.flask.emitter.HtmlEmitterVisitor;
import com.compiler.flask.frontend.FlaskParserFacade;
import com.compiler.flask.visitor.AstPrinterVisitor;
import com.compiler.flask.visitor.SymbolCollectorVisitor;
import com.compiler.flask.symbol.SymbolTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class CompilerDriver {
    public void compile(Path inputScript, Path outputDir) throws IOException {
        String source = Files.readString(inputScript);
        FlaskParserFacade parser = new FlaskParserFacade();
        ProgramNode program = parser.parse(source, inputScript.toString());

        AstPrinterVisitor printer = new AstPrinterVisitor();
        System.out.println("=== AST ===");
        System.out.println(printer.print(program));

        SymbolCollectorVisitor collector = new SymbolCollectorVisitor();
        SymbolTable table = collector.collect(program);
        System.out.println("=== Symbols ===");
        System.out.println(table.dump());

        ScriptContextExtractor contextExtractor = new ScriptContextExtractor();
        RenderContext renderContext = contextExtractor.extract(program);

        HtmlEmitterVisitor emitter = new HtmlEmitterVisitor(renderContext);
        String html = emitter.emit(program);
        Path outputFile = outputDir.resolve(outputFileName(inputScript));
        copyAssets(inputScript, outputDir);
        Files.writeString(outputFile, html);
        System.out.printf("HTML generated at %s%n", outputFile);
    }

    private String outputFileName(Path input) {
        String name = input.getFileName().toString();
        int idx = name.lastIndexOf('.');
        String stem = idx >= 0 ? name.substring(0, idx) : name;
        return stem + ".html";
    }

    private void copyAssets(Path inputScript, Path outputDir) throws IOException {
        Path[] candidates = new Path[] {
                Path.of("src/main/resources/assets"),
                inputScript.getParent() == null ? null : inputScript.getParent().resolve("assets")
        };
        for (Path candidate : candidates) {
            if (candidate != null && Files.exists(candidate)) {
                Path target = outputDir.resolve("assets");
                copyDirectory(candidate, target);
                break;
            }
        }
    }

    private void copyDirectory(Path sourceDir, Path targetDir) throws IOException {
        try (var stream = Files.walk(sourceDir)) {
            stream.forEach(path -> {
                try {
                    Path relative = sourceDir.relativize(path);
                    Path destination = targetDir.resolve(relative.toString());
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(destination);
                    } else {
                        Path parent = destination.getParent();
                        if (parent != null) {
                            Files.createDirectories(parent);
                        }
                        Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to copy assets", ex);
                }
            });
        }
    }
}
