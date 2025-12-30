// AST node for a single CSS rule line.
package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class CssRuleNode extends AstNode {
    private final String rule;

    public CssRuleNode(SourceLocation location, String rule) {
        super("cssRule", location);
        this.rule = rule.trim();
        putAttribute("rule", this.rule);
    }

    public String getRule() {
        return rule;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitCssRule(this);
    }
}
