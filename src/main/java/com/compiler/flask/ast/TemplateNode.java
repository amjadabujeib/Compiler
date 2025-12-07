package com.compiler.flask.ast;

import com.compiler.flask.visitor.AstVisitor;

public final class TemplateNode extends AstNode {
    private final String templateName;

    public TemplateNode(SourceLocation location, String templateName) {
        super("template", location);
        this.templateName = templateName;
        putAttribute("name", templateName);
    }

    public String getTemplateName() {
        return templateName;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitTemplate(this);
    }
}
