// AST node for a css: block that aggregates CSS rules.
package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class CssBlockNode extends AstNode {
    public CssBlockNode(SourceLocation location) {
        super("cssBlock", location);
    }

    public String toCssText() {
        StringBuilder builder = new StringBuilder();
        for (AstNode child : getChildren()) {
            if (child instanceof CssRuleNode rule) {
                builder.append(rule.getRule()).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitCssBlock(this);
    }
}
