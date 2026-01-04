// AST node for Python import statements.
package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

import java.util.ArrayList;
import java.util.List;

public final class PythonImportNode extends PythonNode {
    private final String fromModule;
    private final List<String> imports;

    public PythonImportNode(SourceLocation location, String fromModule, List<String> imports) {
        super("PyImport", location);
        this.fromModule = fromModule;
        this.imports = List.copyOf(new ArrayList<>(imports));
        putAttribute("from", fromModule);
        putAttribute("imports", String.join(", ", this.imports));
    }

    public String getFromModule() {
        return fromModule;
    }

    public List<String> getImports() {
        return imports;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitImport(this);
    }
}
