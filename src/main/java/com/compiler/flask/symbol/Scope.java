package com.compiler.flask.symbol;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

final class Scope {
    private final String name;
    private final int depth;
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();

    Scope(String name, int depth) {
        this.name = name;
        this.depth = depth;
    }

    String getName() {
        return name;
    }

    int getDepth() {
        return depth;
    }

    void declare(Symbol symbol) {
        symbols.putIfAbsent(symbol.getName(), symbol);
    }

    Collection<Symbol> symbols() {
        return symbols.values();
    }
}
