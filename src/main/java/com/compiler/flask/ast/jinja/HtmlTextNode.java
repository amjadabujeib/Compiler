package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public final class HtmlTextNode extends JinjaNode {
    private final String text;
    private final String preview;

    public HtmlTextNode(SourceLocation location, String text, String preview) {
        super("HtmlText", location);
        this.text = text;
        this.preview = preview;
        putAttribute("len", Integer.toString(text != null ? text.length() : 0));
        putAttribute("preview", preview);
    }

    public String getText() {
        return text;
    }

    public String getPreview() {
        return preview;
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitHtmlText(this);
    }
}
