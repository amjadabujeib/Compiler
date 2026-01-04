// Visitor interface for Python AST nodes.
package com.compiler.flask.visitor;

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
import com.compiler.flask.ast.python.PythonPassNode;
import com.compiler.flask.ast.python.PythonReturnNode;
import com.compiler.flask.ast.python.PythonTryNode;

public interface PythonAstVisitor<R> {
    R visitModule(PythonModuleNode node);

    R visitImport(PythonImportNode node);

    R visitGlobal(PythonGlobalNode node);

    R visitFunction(PythonFunctionNode node);

    R visitAssign(PythonAssignNode node);

    R visitExpr(PythonExprNode node);

    R visitReturn(PythonReturnNode node);

    R visitIf(PythonIfNode node);

    R visitElif(PythonElifNode node);

    R visitElse(PythonElseNode node);

    R visitTry(PythonTryNode node);

    R visitExcept(PythonExceptNode node);

    R visitFinally(PythonFinallyNode node);

    R visitPass(PythonPassNode node);
}
