// Base visitor with default traversal for Python AST nodes.
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

public abstract class BasePythonAstVisitor<R> implements PythonAstVisitor<R> {
    protected R visitChildren(PythonNode node) {
        for (AstNode child : node.getChildren()) {
            if (child instanceof PythonNode pythonChild) {
                pythonChild.accept(this);
            }
        }
        return null;
    }

    protected R defaultVisit(PythonNode node) {
        return visitChildren(node);
    }

    @Override
    public R visitModule(PythonModuleNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitImport(PythonImportNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitGlobal(PythonGlobalNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitFunction(PythonFunctionNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitAssign(PythonAssignNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitExpr(PythonExprNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitReturn(PythonReturnNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitIf(PythonIfNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitElif(PythonElifNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitElse(PythonElseNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitTry(PythonTryNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitExcept(PythonExceptNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitFinally(PythonFinallyNode node) {
        return defaultVisit(node);
    }

    @Override
    public R visitPass(PythonPassNode node) {
        return defaultVisit(node);
    }
}
