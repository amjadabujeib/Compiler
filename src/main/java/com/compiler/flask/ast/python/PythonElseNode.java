package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonElseNode extends PythonNode {
    public PythonElseNode(SourceLocation location) {
        super("PyElse", location);
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitElse(this);
    }
}
