package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class JinjaElifNode extends JinjaNode {
    private final String condition;
    private final List<String> refs;

    public JinjaElifNode(SourceLocation location, String condition, List<String> refs) {
        super("JinjaElif", location);
        this.condition = condition;
        this.refs = List.copyOf(new ArrayList<>(refs));
        putAttribute("condition", condition);
        putAttribute("refs", String.join(", ", this.refs));
    }

    public String getCondition() {
        return condition;
    }

    public List<String> getRefs() {
        return refs;
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitElif(this);
    }
}
