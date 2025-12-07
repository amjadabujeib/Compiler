package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class ProgramNode extends AstNode {
    public ProgramNode(SourceLocation location) {
        super("program", location);
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitProgram(this);
    }
}
