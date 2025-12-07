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

public interface AstVisitor<R> {
    R visitProgram(ProgramNode node);

    R visitRoute(RouteNode node);

    R visitTemplate(TemplateNode node);

    R visitPythonLine(PythonLineNode node);

    R visitHtmlLine(HtmlLineNode node);

    R visitJinjaNode(JinjaNode node);

    R visitCssBlock(CssBlockNode node);

    R visitCssRule(CssRuleNode node);

    R visitGeneric(AstNode node);
}
