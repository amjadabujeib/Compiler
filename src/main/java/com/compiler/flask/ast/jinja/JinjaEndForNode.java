// AST node for Jinja endfor markers.
package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public final class JinjaEndForNode extends JinjaNode {
    public JinjaEndForNode(SourceLocation location) {
        super("JinjaEndFor", location);
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitEndFor(this);
    }
}
