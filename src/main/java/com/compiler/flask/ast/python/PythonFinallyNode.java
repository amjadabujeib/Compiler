package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonFinallyNode extends PythonNode {
    public PythonFinallyNode(SourceLocation location) {
        super("PyFinally", location);
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitFinally(this);
    }
}
