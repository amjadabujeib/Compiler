package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonElifNode extends PythonNode {
    private final String condition;

    public PythonElifNode(SourceLocation location, String condition) {
        super("PyElif", location);
        this.condition = condition;
        putAttribute("condition", condition);
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitElif(this);
    }
}
