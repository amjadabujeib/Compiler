// Symbol table with scoped declarations and printing.
package com.compiler.flask.symbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class SymbolTable {
    private final Deque<Scope> scopes = new ArrayDeque<>();
    private final List<Scope> history = new ArrayList<>();

    public void pushScope(String name) {
        int depth = scopes.size();
        Scope scope = new Scope(name, depth);
        scopes.push(scope);
        history.add(scope);
    }

    public void popScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    public void declare(Symbol symbol) {
        if (scopes.isEmpty()) {
            pushScope("global");
        }
        scopes.peek().declare(symbol);
    }

    public void declareInRoot(Symbol symbol) {
        if (history.isEmpty()) {
            pushScope("global");
        }
        Scope root = history.get(0);
        root.declare(symbol);
    }

    public String print() {
        List<Scope> ordered = new ArrayList<>(history);
        StringBuilder builder = new StringBuilder();
        for (Scope scope : ordered) {
            String indent = "  ".repeat(Math.max(0, scope.getDepth()));
            String symbolIndent = indent + "  ";
            builder.append(indent).append("Scope ").append(scope.getName()).append(System.lineSeparator());
            if (scope.symbols().isEmpty()) {
                builder.append(symbolIndent).append("<empty>").append(System.lineSeparator());
                continue;
            }
            for (Symbol symbol : scope.symbols()) {
                builder.append(symbolIndent).append("- ").append(symbol).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
