// AST node for Jinja expression tags.
package com.compiler.flask.ast.jinja;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.JinjaAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class JinjaExprNode extends JinjaNode {
    private final String expression;
    private final List<String> refs;

    public JinjaExprNode(SourceLocation location, String expression, List<String> refs) {
        super("JinjaExpr", location);
        this.expression = expression;
        this.refs = List.copyOf(new ArrayList<>(refs));
        putAttribute("expr", expression);
        putAttribute("refs", String.join(", ", this.refs));
    }

    public String getExpression() {
        return expression;
    }

    public List<String> getRefs() {
        return refs;
    }

    @Override
    public <R> R accept(JinjaAstVisitor<R> visitor) {
        return visitor.visitExpr(this);
    }
}
