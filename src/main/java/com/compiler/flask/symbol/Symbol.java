// the Symbols that (i defined with a specific type) that can be collected (using the collector) to
//form a symbol table which can be printed using the print method.
package com.compiler.flask.symbol;

import com.compiler.flask.ast.SourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Symbol {
    private final String name;
    private final SymbolKind kind;
    private final SourceLocation location;
    private final Map<String, String> metadata = new LinkedHashMap<>();

    public Symbol(String name, SymbolKind kind, SourceLocation location) {
        this.name = name;
        this.kind = kind;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public void putMetadata(String key, String value) {
        metadata.put(key, value);
    }

    @Override
    public String toString() {
        return name + " : " + kind + " @ " + location + (metadata.isEmpty() ? "" : " " + metadata);
    }
}
