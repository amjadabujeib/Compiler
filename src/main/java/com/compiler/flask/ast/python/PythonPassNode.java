// AST node for Python pass statements.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonPassNode extends PythonNode {
    public PythonPassNode(SourceLocation location) {
        super("PyPass", location);
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitPass(this);
    }
}
