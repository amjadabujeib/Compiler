package com.compiler.flask.symbol;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.ast.jinja.CssRuleNode;
import com.compiler.flask.ast.jinja.JinjaElifNode;
import com.compiler.flask.ast.jinja.JinjaExprNode;
import com.compiler.flask.ast.jinja.JinjaForNode;
import com.compiler.flask.ast.jinja.JinjaIfNode;
import com.compiler.flask.ast.jinja.JinjaTemplateNode;
import com.compiler.flask.visitor.BaseJinjaAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class JinjaSymbolCollector extends BaseJinjaAstVisitor<Void> {
    private final SymbolTable table = new SymbolTable();

    public SymbolTable collect(JinjaTemplateNode root) {
        table.pushScope("template:" + root.getFileName());
        table.declare(new Symbol(root.getFileName(), SymbolKind.JINJA_TEMPLATE, root.getLocation()));
        root.accept(this);
        return table;
    }

    @Override
    public Void visitTemplate(JinjaTemplateNode node) {
        return visitChildren(node);
    }

    @Override
    public Void visitFor(JinjaForNode node) {
        for (String target : node.getTargets()) {
            table.declare(new Symbol(target, SymbolKind.JINJA_VAR, node.getLocation()));
        }

        List<String> refs = new ArrayList<>(node.getRefs());
        refs.removeAll(node.getTargets());
        declareRefs(refs, node.getLocation());
        return null;
    }

    @Override
    public Void visitExpr(JinjaExprNode node) {
        declareRefs(node.getRefs(), node.getLocation());
        return null;
    }

    @Override
    public Void visitIf(JinjaIfNode node) {
        declareRefs(node.getRefs(), node.getLocation());
        return null;
    }

    @Override
    public Void visitElif(JinjaElifNode node) {
        declareRefs(node.getRefs(), node.getLocation());
        return null;
    }

    @Override
    public Void visitCssRule(CssRuleNode node) {
        table.declare(new Symbol(node.getRule(), SymbolKind.CSS_RULE, node.getLocation()));
        return null;
    }

    private void declareRefs(List<String> refs, SourceLocation location) {
        for (String ref : refs) {
            table.declare(new Symbol(ref, SymbolKind.JINJA_REF, location));
        }
    }
}
