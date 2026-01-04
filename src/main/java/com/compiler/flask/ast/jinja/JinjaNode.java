// Base node for the Jinja/HTML/CSS AST hierarchy.
package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.AstNode;
import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public abstract class JinjaNode extends AstNode {
    protected JinjaNode(String nodeName, SourceLocation location) {
        super(nodeName, location);
    }

    public abstract <R> R accept(JinjaAstVisitor<R> visitor);
}
