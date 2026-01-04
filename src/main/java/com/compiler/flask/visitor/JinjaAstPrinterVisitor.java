// Visitor that formats Jinja/HTML/CSS AST nodes as text.
package com.compiler.flask.visitor;

import com.compiler.flask.ast.AstNode;
import com.compiler.flask.ast.jinja.CssBlockNode;
import com.compiler.flask.ast.jinja.CssRuleNode;
import com.compiler.flask.ast.jinja.HtmlTextNode;
import com.compiler.flask.ast.jinja.JinjaElifNode;
import com.compiler.flask.ast.jinja.JinjaElseNode;
import com.compiler.flask.ast.jinja.JinjaEndForNode;
import com.compiler.flask.ast.jinja.JinjaEndIfNode;
import com.compiler.flask.ast.jinja.JinjaExprNode;
import com.compiler.flask.ast.jinja.JinjaForNode;
import com.compiler.flask.ast.jinja.JinjaIfNode;
import com.compiler.flask.ast.jinja.JinjaNode;
import com.compiler.flask.ast.jinja.JinjaTemplateNode;

import java.util.Map;

public final class JinjaAstPrinterVisitor extends BaseJinjaAstVisitor<Void> {
    private final StringBuilder builder = new StringBuilder();
    private int depth;

    public String print(JinjaNode root) {
        builder.setLength(0);
        depth = 0;
        root.accept(this);
        return builder.toString();
    }

    private Void writeNode(JinjaNode node) {
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
            if (child instanceof JinjaNode jinjaChild) {
                jinjaChild.accept(this);
            }
        }
        depth--;
        return null;
    }

    @Override
    public Void visitTemplate(JinjaTemplateNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitHtmlText(HtmlTextNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitExpr(JinjaExprNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitFor(JinjaForNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitIf(JinjaIfNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitElif(JinjaElifNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitElse(JinjaElseNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitEndIf(JinjaEndIfNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitEndFor(JinjaEndForNode node) {
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
}
