package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class PythonFunctionNode extends PythonNode {
    private final String name;
    private final List<String> params;
    private final List<String> decorators;

    public PythonFunctionNode(SourceLocation location,
                              String name,
                              List<String> params,
                              List<String> decorators) {
        super("PyFunction", location);
        this.name = name;
        this.params = List.copyOf(new ArrayList<>(params));
        this.decorators = List.copyOf(new ArrayList<>(decorators));
        putAttribute("name", name);
        putAttribute("params", String.join(", ", this.params));
        putAttribute("decorators", String.join(" | ", this.decorators));
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }

    public List<String> getDecorators() {
        return decorators;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitFunction(this);
    }
}
