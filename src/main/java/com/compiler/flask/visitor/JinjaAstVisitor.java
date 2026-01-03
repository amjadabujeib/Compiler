package com.compiler.flask.visitor;

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
import com.compiler.flask.ast.jinja.JinjaTemplateNode;

public interface JinjaAstVisitor<R> {
    R visitTemplate(JinjaTemplateNode node);

    R visitHtmlText(HtmlTextNode node);

    R visitExpr(JinjaExprNode node);

    R visitFor(JinjaForNode node);

    R visitIf(JinjaIfNode node);

    R visitElif(JinjaElifNode node);

    R visitElse(JinjaElseNode node);

    R visitEndIf(JinjaEndIfNode node);

    R visitEndFor(JinjaEndForNode node);

    R visitCssBlock(CssBlockNode node);

    R visitCssRule(CssRuleNode node);
}
