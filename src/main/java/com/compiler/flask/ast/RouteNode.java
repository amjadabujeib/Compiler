// AST node for a route declaration and its path (like route "/products").
package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class RouteNode extends AstNode {
    private final String path;

    public RouteNode(SourceLocation location, String path) {
        super("route", location);
        this.path = path;
        putAttribute("path", path);
    }

    public String getPath() {
        return path;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitRoute(this);
    }
}
