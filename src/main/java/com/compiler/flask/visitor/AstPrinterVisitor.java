package com.compiler.flask.visitor;

import com.compiler.flask.ast.AstNode;
import com.compiler.flask.ast.CssBlockNode;
import com.compiler.flask.ast.CssRuleNode;
import com.compiler.flask.ast.HtmlLineNode;
import com.compiler.flask.ast.JinjaNode;
import com.compiler.flask.ast.ProgramNode;
import com.compiler.flask.ast.PythonLineNode;
import com.compiler.flask.ast.RouteNode;
import com.compiler.flask.ast.TemplateNode;

import java.util.Map;

public final class AstPrinterVisitor extends BaseAstVisitor<Void> {
    private final StringBuilder builder = new StringBuilder();
    private int depth;

    public String print(AstNode root) {
        builder.setLength(0);
        depth = 0;
        root.accept(this);
        return builder.toString();
    }

    private Void writeNode(AstNode node) {
        builder.append("  ".repeat(Math.max(0, depth)))
                .append(node.getNodeName())
                .append(" @ ")
                .append(node.getLocation().line())
                .append(':')
                .append(node.getLocation().column());

        if (!node.getAttributes().isEmpty()) {
            builder.append(" [");
            int index = 0;
            for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
                if (index++ > 0) {
                    builder.append(", ");
                }
                builder.append(entry.getKey()).append('=').append(entry.getValue());
            }
            builder.append(']');
        }
        builder.append(System.lineSeparator());

        depth++;
        for (AstNode child : node.getChildren()) {
            child.accept(this);
        }
        depth--;
        return null;
    }

    @Override
    public Void visitProgram(ProgramNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitRoute(RouteNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitTemplate(TemplateNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitPythonLine(PythonLineNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitHtmlLine(HtmlLineNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitJinjaNode(JinjaNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitCssBlock(CssBlockNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitCssRule(CssRuleNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitGeneric(AstNode node) {
        return writeNode(node);
    }
}
