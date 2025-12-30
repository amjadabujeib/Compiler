// AST node representing a Python-style assignment (the entire products list in our code)
package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class PythonLineNode extends AstNode {
    private final String code;

    public PythonLineNode(SourceLocation location, String code) {
        super("python", location);
        this.code = code.stripTrailing();
        putAttribute("code", this.code);
    }

    public String getCode() {
        return code;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitPythonLine(this);
    }
}
