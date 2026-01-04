// AST node for Python assignments and augassignments.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class PythonAssignNode extends PythonNode {
    private final List<String> targets;
    private final String operator;
    private final String code;

    public PythonAssignNode(SourceLocation location, List<String> targets, String operator, String code) {
        super("PyAssign", location);
        this.targets = List.copyOf(new ArrayList<>(targets));
        this.operator = operator;
        this.code = code;
        putAttribute("targets", String.join(", ", this.targets));
        putAttribute("op", operator);
        putAttribute("code", code);
    }

    public List<String> getTargets() {
        return targets;
    }

    public String getOperator() {
        return operator;
    }

    public String getCode() {
        return code;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitAssign(this);
    }
}
