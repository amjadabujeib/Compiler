package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class JinjaNode extends AstNode {
    public enum Kind {
        EXPR,
        STMT
    }

    private final Kind kind;
    private final String body;

    public JinjaNode(SourceLocation location, Kind kind, String body) {
        super(kind == Kind.EXPR ? "jinjaExpr" : "jinjaStmt", location);
        this.kind = kind;
        this.body = body.trim();
        putAttribute("body", this.body);
    }

    public Kind getKind() {
        return kind;
    }

    public String getBody() {
        return body;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitJinjaNode(this);
    }
}
