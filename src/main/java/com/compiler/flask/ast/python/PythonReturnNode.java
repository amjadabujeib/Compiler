package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonReturnNode extends PythonNode {
    private final String value;

    public PythonReturnNode(SourceLocation location, String value) {
        super("PyReturn", location);
        this.value = value;
        putAttribute("value", value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitReturn(this);
    }
}
