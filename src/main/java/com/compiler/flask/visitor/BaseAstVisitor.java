// Default AST visitor that walks child nodes for each node type.
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

public abstract class BaseAstVisitor<R> implements AstVisitor<R> {
    protected R visitChildren(AstNode node) {
        for (AstNode child : node.getChildren()) {
            child.accept(this);
        }
        return null;
    }

    @Override
    public R visitProgram(ProgramNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitRoute(RouteNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitTemplate(TemplateNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitPythonLine(PythonLineNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitHtmlLine(HtmlLineNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitJinjaNode(JinjaNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitCssBlock(CssBlockNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitCssRule(CssRuleNode node) {
        return visitChildren(node);
    }

}
