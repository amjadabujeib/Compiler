// AST node for Jinja endif markers.
package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public final class JinjaEndIfNode extends JinjaNode {
    public JinjaEndIfNode(SourceLocation location) {
        super("JinjaEndIf", location);
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitEndIf(this);
    }
}
