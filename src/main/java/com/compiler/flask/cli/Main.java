// CLI entry point to compile scripts to HTML or run the demo server.
package com.compiler.flask.cli;

import com.compiler.flask.core.CompilerDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class Main {

    public static void main(String[] args) throws Exception {
        if (args.length >= 1 && Objects.equals(args[0], "--serve")) {
            int port = args.length >= 2 ? Integer.parseInt(args[1]) : 8080;
            com.compiler.flask.server.HttpAppServer server = new com.compiler.flask.server.HttpAppServer(port, Path.of("."));
            server.start();
            return;
        }

        if (args.length < 2) {
            System.err.println("Usage: compiler <script> <outDir>  OR  --serve [port]");
            System.exit(1);
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);

        Files.createDirectories(output);

        CompilerDriver driver = new CompilerDriver();
        driver.compile(input, output);
    }
}
