// AST node for Python expression statements.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonExprNode extends PythonNode {
    private final String code;

    public PythonExprNode(SourceLocation location, String code) {
        super("PyExpr", location);
        this.code = code;
        putAttribute("code", code);
    }

    public String getCode() {
        return code;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitExpr(this);
    }
}
