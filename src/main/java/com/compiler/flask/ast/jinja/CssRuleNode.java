package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public final class CssRuleNode extends JinjaNode {
    private final String rule;

    public CssRuleNode(SourceLocation location, String rule) {
        super("CssRule", location);
        this.rule = rule;
        putAttribute("rule", rule);
    }

    public String getRule() {
        return rule;
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitCssRule(this);
    }
}
