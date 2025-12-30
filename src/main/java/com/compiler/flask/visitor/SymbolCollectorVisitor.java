// Visitor that builds a SymbolTable from routes, templates, variables, and CSS/Jinja usage.
package com.compiler.flask.visitor;

import com.compiler.flask.ast.AstNode;
import com.compiler.flask.ast.CssRuleNode;
import com.compiler.flask.ast.JinjaNode;
import com.compiler.flask.ast.PythonLineNode;
import com.compiler.flask.ast.RouteNode;
import com.compiler.flask.ast.TemplateNode;
import com.compiler.flask.symbol.Symbol;
import com.compiler.flask.symbol.SymbolKind;
import com.compiler.flask.symbol.SymbolTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SymbolCollectorVisitor extends BaseAstVisitor<Void> {
    private static final Pattern ASSIGNMENT = Pattern.compile("(?s)^(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s*=.*$");

    private final SymbolTable symbolTable = new SymbolTable();

    public SymbolTable collect(AstNode root) {
        symbolTable.pushScope("program");
        root.accept(this);
        return symbolTable;
    }

    @Override
    public Void visitRoute(RouteNode node) {
        symbolTable.declareInRoot(new Symbol(node.getPath(), SymbolKind.ROUTE, node.getLocation()));
        symbolTable.pushScope("route:" + node.getPath());
        super.visitRoute(node);
        symbolTable.popScope();
        return null;
    }

    @Override
    public Void visitTemplate(TemplateNode node) {
        symbolTable.declare(new Symbol(node.getTemplateName(), SymbolKind.TEMPLATE, node.getLocation()));
        symbolTable.pushScope("template:" + node.getTemplateName());
        super.visitTemplate(node);
        symbolTable.popScope();
        return null;
    }

    @Override
    public Void visitPythonLine(PythonLineNode node) {
        Matcher matcher = ASSIGNMENT.matcher(node.getCode().trim());
        if (matcher.matches()) {
            String name = matcher.group("name");
            Symbol symbol = new Symbol(name, SymbolKind.VARIABLE, node.getLocation());
            symbol.putMetadata("code", node.getCode().trim());
            symbolTable.declare(symbol);
        }
        return null;
    }

    @Override
    public Void visitCssRule(CssRuleNode node) {
        Symbol symbol = new Symbol(node.getRule(), SymbolKind.CSS_RULE, node.getLocation());
        symbolTable.declare(symbol);
        return null;
    }

    @Override
    public Void visitJinjaNode(JinjaNode node) {
        if (node.getKind() == JinjaNode.Kind.STMT && node.getBody().startsWith("for")) {
            String[] segments = node.getBody().split("\\s+");
            if (segments.length >= 3) {
                Symbol symbol = new Symbol(segments[1], SymbolKind.VARIABLE, node.getLocation());
                symbol.putMetadata("context", "jinja-for");
                symbolTable.declare(symbol);
            }
        }
        return null;
    }
}
