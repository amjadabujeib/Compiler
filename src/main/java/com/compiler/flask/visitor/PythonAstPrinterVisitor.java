package com.compiler.flask.visitor;

import com.compiler.flask.ast.AstNode;
import com.compiler.flask.ast.python.PythonAssignNode;
import com.compiler.flask.ast.python.PythonElifNode;
import com.compiler.flask.ast.python.PythonElseNode;
import com.compiler.flask.ast.python.PythonExceptNode;
import com.compiler.flask.ast.python.PythonExprNode;
import com.compiler.flask.ast.python.PythonFinallyNode;
import com.compiler.flask.ast.python.PythonFunctionNode;
import com.compiler.flask.ast.python.PythonGlobalNode;
import com.compiler.flask.ast.python.PythonIfNode;
import com.compiler.flask.ast.python.PythonImportNode;
import com.compiler.flask.ast.python.PythonModuleNode;
import com.compiler.flask.ast.python.PythonNode;
import com.compiler.flask.ast.python.PythonPassNode;
import com.compiler.flask.ast.python.PythonReturnNode;
import com.compiler.flask.ast.python.PythonTryNode;

import java.util.Map;

public final class PythonAstPrinterVisitor extends BasePythonAstVisitor<Void> {
    private final StringBuilder builder = new StringBuilder();
    private int depth;

    public String print(PythonNode root) {
        builder.setLength(0);
        depth = 0;
        root.accept(this);
        return builder.toString();
    }

    private Void writeNode(PythonNode node) {
        builder.append("  ".repeat(Math.max(0, depth)))
                .append(node.getNodeName())
                .append(" @ ")
                .append(node.getLocation().line())
                .append(':')
                .append(node.getLocation().column());

        if (!node.getAttributes().isEmpty()) {
            builder.append(" [");
            int index = 0;
            for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
                if (index++ > 0) {
                    builder.append(", ");
                }
                builder.append(entry.getKey()).append('=').append(entry.getValue());
            }
            builder.append(']');
        }
        builder.append(System.lineSeparator());

        depth++;
        for (AstNode child : node.getChildren()) {
            if (child instanceof PythonNode pythonChild) {
                pythonChild.accept(this);
            }
        }
        depth--;
        return null;
    }

    @Override
    public Void visitModule(PythonModuleNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitImport(PythonImportNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitGlobal(PythonGlobalNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitFunction(PythonFunctionNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitAssign(PythonAssignNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitExpr(PythonExprNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitReturn(PythonReturnNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitIf(PythonIfNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitElif(PythonElifNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitElse(PythonElseNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitTry(PythonTryNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitExcept(PythonExceptNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitFinally(PythonFinallyNode node) {
        return writeNode(node);
    }

    @Override
    public Void visitPass(PythonPassNode node) {
        return writeNode(node);
    }
}
