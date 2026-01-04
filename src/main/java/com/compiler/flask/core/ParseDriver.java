// Coordinates parsing files, building ASTs, and printing symbols.
package com.compiler.flask.core;

import com.compiler.flask.ast.jinja.JinjaTemplateNode;
import com.compiler.flask.ast.python.PythonModuleNode;
import com.compiler.flask.frontend.JinjaAstBuilder;
import com.compiler.flask.frontend.PythonAstBuilder;
import com.compiler.flask.grammar.FlaskPythonLexer;
import com.compiler.flask.grammar.FlaskPythonParser;
import com.compiler.flask.grammar.JinjaHtmlLexer;
import com.compiler.flask.grammar.JinjaHtmlParser;
import com.compiler.flask.symbol.JinjaSymbolCollector;
import com.compiler.flask.symbol.PythonSymbolCollector;
import com.compiler.flask.symbol.SymbolTable;
import com.compiler.flask.visitor.JinjaAstPrinterVisitor;
import com.compiler.flask.visitor.PythonAstPrinterVisitor;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class ParseDriver {

    public void parse(Path appFile, Path templatesDir) throws IOException {
        PythonAstPrinterVisitor pythonPrinter = new PythonAstPrinterVisitor();
        JinjaAstPrinterVisitor jinjaPrinter = new JinjaAstPrinterVisitor();
        parsePython(appFile, pythonPrinter);

        parseTemplate(templatesDir, jinjaPrinter);

    }

    private void parsePython(Path file, PythonAstPrinterVisitor printer) throws IOException {
        String source = Files.readString(file);
        FlaskPythonLexer lexer = new FlaskPythonLexer(CharStreams.fromString(source));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FlaskPythonParser parser = new FlaskPythonParser(tokens);

        FlaskPythonParser.File_inputContext tree = parser.file_input();

        PythonModuleNode ast = new PythonAstBuilder(file.toString(), source).build(tree);
        SymbolTable symbols = new PythonSymbolCollector().collect(ast);

        System.out.println("=== Python AST ===");
        System.out.print(printer.print(ast));
        System.out.println("=== Python Symbols ===");
        System.out.print(symbols.print());
    }

    private void parseTemplate(Path file, JinjaAstPrinterVisitor printer) throws IOException {
        String source = Files.readString(file);
        JinjaHtmlLexer lexer = new JinjaHtmlLexer(CharStreams.fromString(source));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JinjaHtmlParser parser = new JinjaHtmlParser(tokens);

        JinjaHtmlParser.TemplateContext tree = parser.template();

        JinjaTemplateNode ast = new JinjaAstBuilder(file.toString(), source, tokens).build(tree);
        SymbolTable symbols = new JinjaSymbolCollector().collect(ast);

        System.out.printf("=== Template AST: %s ===%n", file);
        System.out.print(printer.print(ast));
        System.out.printf("=== Template Symbols: %s ===%n", file);
        System.out.print(symbols.print());
    }

}
