// AST node for Python try blocks.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonTryNode extends PythonNode {
    public PythonTryNode(SourceLocation location) {
        super("PyTry", location);
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitTry(this);
    }
}
