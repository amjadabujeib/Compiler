// Root AST node for a Jinja template file.
package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

public final class JinjaTemplateNode extends JinjaNode {
    private final String fileName;

    public JinjaTemplateNode(SourceLocation location, String fileName) {
        super("JinjaTemplate", location);
        this.fileName = fileName;
        putAttribute("file", fileName);
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitTemplate(this);
    }
}
