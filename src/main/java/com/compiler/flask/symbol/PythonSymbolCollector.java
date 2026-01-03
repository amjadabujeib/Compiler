package com.compiler.flask.symbol;

import com.compiler.flask.ast.python.PythonAssignNode;
import com.compiler.flask.ast.python.PythonFunctionNode;
import com.compiler.flask.ast.python.PythonGlobalNode;
import com.compiler.flask.ast.python.PythonImportNode;
import com.compiler.flask.ast.python.PythonModuleNode;
import com.compiler.flask.visitor.BasePythonAstVisitor;

public final class PythonSymbolCollector extends BasePythonAstVisitor<Void> {
    private final SymbolTable table = new SymbolTable();

    public SymbolTable collect(PythonModuleNode root) {
        table.pushScope("python:" + root.getFileName());
        table.declare(new Symbol(root.getFileName(), SymbolKind.PY_MODULE, root.getLocation()));
        root.accept(this);
        return table;
    }

    @Override
    public Void visitModule(PythonModuleNode node) {
        return visitChildren(node);
    }

    @Override
    public Void visitFunction(PythonFunctionNode node) {
        Symbol symbol = new Symbol(node.getName(), SymbolKind.PY_FUNCTION, node.getLocation());
        if (!node.getDecorators().isEmpty()) {
            symbol.putMetadata("decorators", String.join(" | ", node.getDecorators()));
        }
        table.declare(symbol);
        table.pushScope("function:" + node.getName());
        for (String param : node.getParams()) {
            table.declare(new Symbol(param, SymbolKind.PY_PARAM, node.getLocation()));
        }
        visitChildren(node);
        table.popScope();
        return null;
    }

    @Override
    public Void visitAssign(PythonAssignNode node) {
        for (String target : node.getTargets()) {
            table.declare(new Symbol(target, SymbolKind.PY_VARIABLE, node.getLocation()));
        }
        return null;
    }

    @Override
    public Void visitImport(PythonImportNode node) {
        String from = node.getFromModule();
        for (String name : node.getImports()) {
            Symbol symbol = new Symbol(name, SymbolKind.PY_IMPORT, node.getLocation());
            if (from != null && !from.isBlank()) {
                symbol.putMetadata("from", from);
            }
            table.declare(symbol);
        }
        return null;
    }

    @Override
    public Void visitGlobal(PythonGlobalNode node) {
        for (String name : node.getNames()) {
            table.declareInRoot(new Symbol(name, SymbolKind.PY_GLOBAL, node.getLocation()));
        }
        return null;
    }
}
