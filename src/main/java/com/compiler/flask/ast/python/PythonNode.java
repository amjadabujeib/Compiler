// Base node for the Python AST hierarchy.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.AstNode;
import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public abstract class PythonNode extends AstNode {
    protected PythonNode(String nodeName, SourceLocation location) {
        super(nodeName, location);
    }

    public abstract <R> R accept(PythonAstVisitor<R> visitor);
}
