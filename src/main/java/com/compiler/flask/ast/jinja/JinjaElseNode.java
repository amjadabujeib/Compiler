package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public final class JinjaElseNode extends JinjaNode {
    public JinjaElseNode(SourceLocation location) {
        super("JinjaElse", location);
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitElse(this);
    }
}
