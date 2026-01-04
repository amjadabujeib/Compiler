// AST node for Python global declarations.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class PythonGlobalNode extends PythonNode {
    private final List<String> names;

    public PythonGlobalNode(SourceLocation location, List<String> names) {
        super("PyGlobal", location);
        this.names = List.copyOf(new ArrayList<>(names));
        putAttribute("names", String.join(", ", this.names));
    }

    public List<String> getNames() {
        return names;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitGlobal(this);
    }
}
