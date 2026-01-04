// Base visitor with default traversal for Jinja/HTML/CSS AST nodes.
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

public abstract class BaseJinjaAstVisitor<R> implements JinjaAstVisitor<R> {
    protected R visitChildren(JinjaNode node) {
        for (AstNode child : node.getChildren()) {
            if (child instanceof JinjaNode jinjaChild) {
                jinjaChild.accept(this);
            }
        }
        return null;
    }

    protected R defaultVisit(JinjaNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitTemplate(JinjaTemplateNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitHtmlText(HtmlTextNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitExpr(JinjaExprNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitFor(JinjaForNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitIf(JinjaIfNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitElif(JinjaElifNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitElse(JinjaElseNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitEndIf(JinjaEndIfNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitEndFor(JinjaEndForNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitCssBlock(CssBlockNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitCssRule(CssRuleNode node) {
        return defaultVisit(node);
    }
}
