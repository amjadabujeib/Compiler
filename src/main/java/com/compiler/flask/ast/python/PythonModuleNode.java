package com.compiler.flask.ast.python;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.visitor.PythonAstVisitor;

public final class PythonModuleNode extends PythonNode {
    private final String fileName;

    public PythonModuleNode(SourceLocation location, String fileName) {
        super("PythonModule", location);
        this.fileName = fileName;
        putAttribute("file", fileName);
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public <R> R accept(PythonAstVisitor<R> visitor) {
        return visitor.visitModule(this);
    }
}
