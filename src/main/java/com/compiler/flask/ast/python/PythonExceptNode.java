// AST node for Python except blocks.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonExceptNode extends PythonNode {
    private final String typeName;
    private final String alias;

    public PythonExceptNode(SourceLocation location, String typeName, String alias) {
        super("PyExcept", location);
        this.typeName = typeName;
        this.alias = alias;
        putAttribute("type", typeName);
        putAttribute("as", alias);
    }

    public String getTypeName() {
        return typeName;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitExcept(this);
    }
}
