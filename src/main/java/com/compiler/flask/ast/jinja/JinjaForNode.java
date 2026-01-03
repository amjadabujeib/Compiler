package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class JinjaForNode extends JinjaNode {
    private final List<String> targets;
    private final String expression;
    private final List<String> refs;

    public JinjaForNode(SourceLocation location,
                        List<String> targets,
                        String expression,
                        List<String> refs) {
        super("JinjaFor", location);
        this.targets = List.copyOf(new ArrayList<>(targets));
        this.expression = expression;
        this.refs = List.copyOf(new ArrayList<>(refs));
        putAttribute("targets", String.join(", ", this.targets));
        putAttribute("expr", expression);
        putAttribute("refs", String.join(", ", this.refs));
    }

    public List<String> getTargets() {
        return targets;
    }

    public String getExpression() {
        return expression;
    }

    public List<String> getRefs() {
        return refs;
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitFor(this);
    }
}
