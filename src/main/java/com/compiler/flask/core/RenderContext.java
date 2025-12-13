package com.compiler.flask.core;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RenderContext {
    private final Map<String, Object> globals = new LinkedHashMap<>();

    public void setVariable(String name, Object value) {
        globals.put(name, value);
    }

    public Object getVariable(String name) {
        return globals.get(name);
    }

    public Map<String, Object> globals() {
        return globals;
    }
}
