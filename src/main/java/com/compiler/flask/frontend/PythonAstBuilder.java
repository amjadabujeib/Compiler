// Builds a Python AST from the ANTLR parse tree.
package com.compiler.flask.frontend;

import com.compiler.flask.ast.SourceLocation;
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
import com.compiler.flask.grammar.FlaskPythonParser;
import com.compiler.flask.grammar.FlaskPythonParserBaseVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class PythonAstBuilder extends FlaskPythonParserBaseVisitor<PythonNode> {
    private static final Pattern SIMPLE_NAME = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private final String fileName;
    private final String source;

    public PythonAstBuilder(String fileName, String source) {
        this.fileName = fileName;
        this.source = source;
    }

    public PythonModuleNode build(FlaskPythonParser.File_inputContext ctx) {
        return (PythonModuleNode) visitFile_input(ctx);
    }

    @Override
    public PythonNode visitFile_input(FlaskPythonParser.File_inputContext ctx) {
        PythonModuleNode module = new PythonModuleNode(location(ctx), fileName);
        for (FlaskPythonParser.StmtContext stmt : ctx.stmt()) {
            PythonNode child = visit(stmt);
            module.addChild(child);
        }
        return module;
    }

    @Override
    public PythonNode visitStmt(FlaskPythonParser.StmtContext ctx) {
        if (ctx.simple_stmt() != null) {
            return visit(ctx.simple_stmt());
        }
        if (ctx.compound_stmt() != null) {
            return visit(ctx.compound_stmt());
        }
        return null;
    }

    @Override
    public PythonNode visitSimple_stmt(FlaskPythonParser.Simple_stmtContext ctx) {
        if (ctx.small_stmt() != null) {
            return visit(ctx.small_stmt());
        }
        return null;
    }

    @Override
    public PythonNode visitSmall_stmt(FlaskPythonParser.Small_stmtContext ctx) {
        if (ctx.expr_stmt() != null) {
            return visit(ctx.expr_stmt());
        }
        if (ctx.return_stmt() != null) {
            return visit(ctx.return_stmt());
        }
        if (ctx.import_stmt() != null) {
            return visit(ctx.import_stmt());
        }
        if (ctx.global_stmt() != null) {
            return visit(ctx.global_stmt());
        }
        if (ctx.pass_stmt() != null) {
            return visit(ctx.pass_stmt());
        }
        return null;
    }

    @Override
    public PythonNode visitImport_stmt(FlaskPythonParser.Import_stmtContext ctx) {
        if (ctx.import_name() != null) {
            return visit(ctx.import_name());
        }
        if (ctx.from_import_stmt() != null) {
            return visit(ctx.from_import_stmt());
        }
        return null;
    }

    @Override
    public PythonNode visitImport_name(FlaskPythonParser.Import_nameContext ctx) {
        List<String> names = new ArrayList<>();
        for (FlaskPythonParser.Dotted_nameContext name : ctx.dotted_name()) {
            names.add(name.getText());
        }
        return new PythonImportNode(location(ctx), null, names);
    }

    @Override
    public PythonNode visitFrom_import_stmt(FlaskPythonParser.From_import_stmtContext ctx) {
        List<String> names = new ArrayList<>();
        for (FlaskPythonParser.Import_as_nameContext imp : ctx.import_as_names().import_as_name()) {
            String name = imp.NAME(0).getText();
            if (imp.NAME().size() > 1) {
                name = name + " as " + imp.NAME(1).getText();
            }
            names.add(name);
        }
        return new PythonImportNode(location(ctx), ctx.dotted_name().getText(), names);
    }

    @Override
    public PythonNode visitGlobal_stmt(FlaskPythonParser.Global_stmtContext ctx) {
        List<String> names = new ArrayList<>();
        for (TerminalNode name : ctx.NAME()) {
            names.add(name.getText());
        }
        return new PythonGlobalNode(location(ctx), names);
    }

    @Override
    public PythonNode visitReturn_stmt(FlaskPythonParser.Return_stmtContext ctx) {
        String value = ctx.testlist() != null ? capture(ctx.testlist()) : "";
        return new PythonReturnNode(location(ctx), value);
    }

    @Override
    public PythonNode visitExpr_stmt(FlaskPythonParser.Expr_stmtContext ctx) {
        boolean isAssign = ctx.augassign() != null || !ctx.ASSIGN().isEmpty();
        if (!isAssign) {
            return new PythonExprNode(location(ctx), capture(ctx));
        }

        List<String> targets = extractTargets(ctx.testlist(0));
        String op = ctx.augassign() != null ? ctx.augassign().getText() : "";
        return new PythonAssignNode(location(ctx), targets, op, capture(ctx));
    }

    @Override
    public PythonNode visitPass_stmt(FlaskPythonParser.Pass_stmtContext ctx) {
        return new PythonPassNode(location(ctx));
    }

    @Override
    public PythonNode visitFuncdef(FlaskPythonParser.FuncdefContext ctx) {
        String name = ctx.NAME().getText();

        List<String> params = new ArrayList<>();
        if (ctx.parameters().paramlist() != null) {
            for (TerminalNode paramName : ctx.parameters().paramlist().NAME()) {
                params.add(paramName.getText());
            }
        }

        List<String> decorators = new ArrayList<>();
        if (ctx.decorators() != null) {
            for (FlaskPythonParser.DecoratorContext dec : ctx.decorators().decorator()) {
                decorators.add(capture(dec));
            }
        }

        PythonFunctionNode node = new PythonFunctionNode(location(ctx), name, params, decorators);
        addSuiteChildren(node, ctx.suite());
        return node;
    }

    @Override
    public PythonNode visitIf_stmt(FlaskPythonParser.If_stmtContext ctx) {
        PythonIfNode node = new PythonIfNode(location(ctx), capture(ctx.test(0)));
        addSuiteChildren(node, ctx.suite(0));

        int elifCount = ctx.ELIF().size();
        for (int i = 0; i < elifCount; i++) {
            PythonElifNode elifNode = new PythonElifNode(location(ctx.test(i + 1)), capture(ctx.test(i + 1)));
            addSuiteChildren(elifNode, ctx.suite(i + 1));
            node.addChild(elifNode);
        }

        if (ctx.ELSE() != null) {
            PythonElseNode elseNode = new PythonElseNode(location(ctx));
            addSuiteChildren(elseNode, ctx.suite(ctx.suite().size() - 1));
            node.addChild(elseNode);
        }
        return node;
    }

    @Override
    public PythonNode visitTry_stmt(FlaskPythonParser.Try_stmtContext ctx) {
        PythonTryNode node = new PythonTryNode(location(ctx));
        addSuiteChildren(node, ctx.suite(0));

        int exceptCount = ctx.except_clause().size();
        for (int i = 0; i < exceptCount; i++) {
            FlaskPythonParser.Except_clauseContext clause = ctx.except_clause(i);
            String type = clause.test() != null ? capture(clause.test()) : "";
            String alias = clause.NAME() != null ? clause.NAME().getText() : "";
            PythonExceptNode exceptNode = new PythonExceptNode(location(clause), type, alias);
            addSuiteChildren(exceptNode, ctx.suite(i + 1));
            node.addChild(exceptNode);
        }

        if (ctx.FINALLY() != null) {
            PythonFinallyNode finallyNode = new PythonFinallyNode(location(ctx));
            addSuiteChildren(finallyNode, ctx.suite(ctx.suite().size() - 1));
            node.addChild(finallyNode);
        }
        return node;
    }

    private void addSuiteChildren(PythonNode parent, FlaskPythonParser.SuiteContext suite) {
        if (suite == null) {
            return;
        }
        for (FlaskPythonParser.StmtContext stmt : suite.stmt()) {
            PythonNode child = visit(stmt);
            parent.addChild(child);
        }
    }

    private List<String> extractTargets(FlaskPythonParser.TestlistContext ctx) {
        List<String> targets = new ArrayList<>();
        if (ctx == null) {
            return targets;
        }
        for (FlaskPythonParser.TestContext test : ctx.test()) {
            String text = test.getText();
            if (SIMPLE_NAME.matcher(text).matches()) {
                targets.add(text);
            }
        }
        return targets;
    }

    private SourceLocation location(ParserRuleContext ctx) {
        Token token = ctx.getStart();
        return new SourceLocation(fileName, token.getLine(), token.getCharPositionInLine() + 1);
    }

    private String capture(ParserRuleContext ctx) {
        if (ctx == null) {
            return "";
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        int startIdx = Math.max(0, start.getStartIndex());
        int stopIdx = Math.max(startIdx, stop.getStopIndex());
        if (startIdx >= source.length()) {
            return "";
        }
        stopIdx = Math.min(stopIdx, source.length() - 1);
        String snippet = source.substring(startIdx, stopIdx + 1);
        return snippet.replaceAll("[\\r\\n]+$", "").trim();
    }
}
