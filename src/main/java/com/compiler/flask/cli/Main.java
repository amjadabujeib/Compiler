// CLI entry point to parse a Flask app.py and its Jinja templates.
package com.compiler.flask.cli;

import com.compiler.flask.core.ParseDriver;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {

    public static void main(String[] args) throws Exception {
        if (args.length >= 1 && "--python-only".equals(args[0])) {
            if (args.length < 2) {
                System.err.println("Usage: compiler --python-only <app.txt>");
                System.exit(1);
            }
            Path appFile = Path.of(args[1]);
            if (!Files.exists(appFile)) {
                System.err.printf("App file not found: %s%n", appFile);
                System.exit(1);
            }
            ParseDriver driver = new ParseDriver();
            try {
                driver.parsePythonOnly(appFile);
            } catch (ParseCancellationException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
            return;
        }

        if (args.length < 2) {
            System.err.println("Usage: compiler <app.txt> <templatesDirOrFile>");
            System.err.println("   or: compiler --python-only <app.txt>");
            System.exit(1);
        }

        Path appFile = Path.of(args[0]);
        Path templatesDir = Path.of(args[1]);

        if (!Files.exists(appFile)) {
            System.err.printf("App file not found: %s%n", appFile);
            System.exit(1);
        }

        ParseDriver driver = new ParseDriver();
        try {
            driver.parse(appFile, templatesDir);
        } catch (ParseCancellationException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
