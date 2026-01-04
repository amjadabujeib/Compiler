// Builds a Jinja/HTML/CSS AST from the ANTLR parse tree.
package com.compiler.flask.frontend;

import com.compiler.flask.ast.SourceLocation;
import com.compiler.flask.ast.jinja.CssBlockNode;
import com.compiler.flask.ast.jinja.CssRuleNode;
import com.compiler.flask.ast.jinja.HtmlTextNode;
import com.compiler.flask.ast.jinja.JinjaElifNode;
import com.compiler.flask.ast.jinja.JinjaElseNode;
import com.compiler.flask.ast.jinja.JinjaEndForNode;
import com.compiler.flask.ast.jinja.JinjaEndIfNode;
import com.compiler.flask.ast.jinja.JinjaExprNode;
import com.compiler.flask.ast.jinja.JinjaForNode;
import com.compiler.flask.ast.jinja.JinjaIfNode;
import com.compiler.flask.ast.jinja.JinjaNode;
import com.compiler.flask.ast.jinja.JinjaTemplateNode;
import com.compiler.flask.grammar.JinjaHtmlLexer;
import com.compiler.flask.grammar.JinjaHtmlParser;
import com.compiler.flask.grammar.JinjaHtmlParserBaseVisitor;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public final class JinjaAstBuilder extends JinjaHtmlParserBaseVisitor<JinjaNode> {
    private static final int PREVIEW_LIMIT = 80;
    private final String fileName;
    private final String source;
    private final CommonTokenStream tokens;

    public JinjaAstBuilder(String fileName, String source, CommonTokenStream tokens) {
        this.fileName = fileName;
        this.source = source;
        this.tokens = tokens;
    }

    public JinjaTemplateNode build(JinjaHtmlParser.TemplateContext ctx) {
        return (JinjaTemplateNode) visitTemplate(ctx);
    }

    @Override
    public JinjaNode visitTemplate(JinjaHtmlParser.TemplateContext ctx) {
        JinjaTemplateNode root = new JinjaTemplateNode(location(ctx), fileName);

        for (ParseTree child : ctx.children) {
            if (child instanceof TerminalNode) {
                continue;
            }
            if (child instanceof ParserRuleContext rule) {
                JinjaNode node = visit(rule);
                root.addChild(node);
            }
        }
        return root;
    }

    @Override
    public JinjaNode visitHtmlChunk(JinjaHtmlParser.HtmlChunkContext ctx) {
        String raw = captureRaw(ctx);
        return new HtmlTextNode(location(ctx), raw, preview(raw));
    }

    @Override
    public JinjaNode visitStyleBlock(JinjaHtmlParser.StyleBlockContext ctx) {
        String raw = captureRaw(ctx);
        HtmlTextNode node = new HtmlTextNode(location(ctx), raw, preview(raw));
        String css = captureCssBody(ctx).trim();
        CssBlockNode block = new CssBlockNode(location(ctx), css, preview(css));
        for (JinjaHtmlParser.CssRuleContext rule : ctx.cssRule()) {
            String ruleText = compactWhitespace(captureRaw(rule));
            if (!ruleText.isEmpty()) {
                block.addChild(new CssRuleNode(location(rule), ruleText));
            }
        }
        node.addChild(block);
        return node;
    }

    @Override
    public JinjaNode visitJinjaExpr(JinjaHtmlParser.JinjaExprContext ctx) {
        String expr = capture(ctx.jinjaExpression());
        List<String> refs = collectNames(ctx.jinjaExpression());
        return new JinjaExprNode(location(ctx), expr, refs);
    }

    @Override
    public JinjaNode visitJinjaStmt(JinjaHtmlParser.JinjaStmtContext ctx) {
        JinjaHtmlParser.JinjaStatementContext stmt = ctx.jinjaStatement();
        if (stmt.for_stmt() != null) {
            return buildFor(stmt.for_stmt());
        }
        if (stmt.if_stmt() != null) {
            return buildIf(stmt.if_stmt().jinjaExpression());
        }
        if (stmt.elif_stmt() != null) {
            return buildElif(stmt.elif_stmt().jinjaExpression());
        }
        if (stmt.else_stmt() != null) {
            return new JinjaElseNode(location(ctx));
        }
        if (stmt.endif_stmt() != null) {
            return new JinjaEndIfNode(location(ctx));
        }
        if (stmt.endfor_stmt() != null) {
            return new JinjaEndForNode(location(ctx));
        }
        return null;
    }

    private JinjaNode buildFor(JinjaHtmlParser.For_stmtContext ctx) {
        List<String> targets = new ArrayList<>();
        for (TerminalNode name : ctx.target_list().NAME()) {
            targets.add(name.getText());
        }
        String expr = capture(ctx.jinjaExpression());
        List<String> refs = collectNames(ctx.jinjaExpression());
        refs.removeAll(targets);
        return new JinjaForNode(location(ctx), targets, expr, refs);
    }

    private JinjaNode buildIf(JinjaHtmlParser.JinjaExpressionContext exprCtx) {
        List<String> refs = collectNames(exprCtx);
        return new JinjaIfNode(location(exprCtx), capture(exprCtx), refs);
    }

    private JinjaNode buildElif(JinjaHtmlParser.JinjaExpressionContext exprCtx) {
        List<String> refs = collectNames(exprCtx);
        return new JinjaElifNode(location(exprCtx), capture(exprCtx), refs);
    }

    private List<String> collectNames(ParserRuleContext ctx) {
        List<String> names = new ArrayList<>();
        if (ctx == null) {
            return names;
        }
        int start = ctx.getStart().getTokenIndex();
        int stop = ctx.getStop().getTokenIndex();
        List<Token> slice = tokens.getTokens(start, stop);
        if (slice == null) {
            return names;
        }
        Token previous = null;
        for (Token token : slice) {
            if (token.getType() == JinjaHtmlLexer.NAME) {
                if (previous != null && previous.getType() == JinjaHtmlLexer.DOT) {
                    previous = token;
                    continue;
                }
                names.add(token.getText());
            }
            previous = token;
        }
        return names;
    }

    private String preview(String text) {
        if (text == null) {
            return "";
        }
        String singleLine = text.replace("\r", "").replace("\n", "\\n");
        if (singleLine.length() > PREVIEW_LIMIT) {
            return singleLine.substring(0, PREVIEW_LIMIT) + "...";
        }
        return singleLine;
    }

    private String compactWhitespace(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("\\s+", " ").trim();
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

    private String captureRaw(ParserRuleContext ctx) {
        if (ctx == null) {
            return "";
        }
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        return captureRange(start, stop);
    }

    private String captureCssBody(JinjaHtmlParser.StyleBlockContext ctx) {
        if (ctx.cssRule().isEmpty()) {
            return "";
        }
        Token start = ctx.cssRule(0).getStart();
        Token stop = ctx.cssRule(ctx.cssRule().size() - 1).getStop();
        return captureRange(start, stop);
    }

    private String captureRange(Token start, Token stop) {
        if (start == null || stop == null) {
            return "";
        }
        int startIdx = Math.max(0, start.getStartIndex());
        int stopIdx = Math.max(startIdx, stop.getStopIndex());
        if (startIdx >= source.length()) {
            return "";
        }
        stopIdx = Math.min(stopIdx, source.length() - 1);
        return source.substring(startIdx, stopIdx + 1);
    }
}
