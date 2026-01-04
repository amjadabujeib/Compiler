// AST node for a CSS <style> block.
package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public final class CssBlockNode extends JinjaNode {
    private final String css;
    private final String preview;

    public CssBlockNode(SourceLocation location, String css, String preview) {
        super("CssBlock", location);
        this.css = css;
        this.preview = preview;
        putAttribute("len", Integer.toString(css != null ? css.length() : 0));
        putAttribute("preview", preview);
    }

    public String getCss() {
        return css;
    }

    public String getPreview() {
        return preview;
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitCssBlock(this);
    }
}
