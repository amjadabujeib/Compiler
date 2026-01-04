// Base AST node with location, children, and attributes.
package com.compiler.flask.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AstNode {
    private final String nodeName;
    private final SourceLocation location;
    private final List<AstNode> children = new ArrayList<>();
    private final Map<String, String> attributes = new LinkedHashMap<>();

    protected AstNode(String nodeName, SourceLocation location) {
        this.nodeName = nodeName;
        this.location = location;
    }

    public String getNodeName() {
        return nodeName;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public List<AstNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void addChild(AstNode child) {
        if (child != null) {
            children.add(child);
        }
    }

    public void putAttribute(String key, String value) {
        if (key != null && value != null && !value.isBlank()) {
            attributes.put(key, value);
        }
    }
}
