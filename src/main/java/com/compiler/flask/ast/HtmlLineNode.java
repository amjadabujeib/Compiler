// AST node for a raw HTML line inside a template.
package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class HtmlLineNode extends AstNode {
    private final String content;

    public HtmlLineNode(SourceLocation location, String content) {
        super("html", location);
        this.content = content;
        putAttribute("content", content.trim());
    }

    public String getContent() {
        return content;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitHtmlLine(this);
    }
}
