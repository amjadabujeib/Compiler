package com.compiler.flask.frontend;

import com.compiler.flask.ast.*;
import com.compiler.flask.grammar.*;
import com.compiler.flask.grammar.FlaskLangParser.*;
import org.antlr.v4.runtime.*;

import java.util.List;

public final class FlaskParserFacade {
    public ProgramNode parse(String source, String fileName) {
        FlaskLangLexer lexer = new FlaskLangLexer(CharStreams.fromString(source));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FlaskLangParser parser = new FlaskLangParser(tokens);
        ProgramContext context = parser.program();
        AstBuilder builder = new AstBuilder(tokens, fileName, source);
        return builder.visitProgram(context);
    }

    private static final class AstBuilder extends FlaskLangBaseVisitor<AstNode> {
        private final CommonTokenStream tokens;
        private final String fileName;
        private final String source;

        AstBuilder(CommonTokenStream tokens, String fileName, String source) {
            this.tokens = tokens;
            this.fileName = fileName;
            this.source = source;
        }

        @Override
        public ProgramNode visitProgram(ProgramContext ctx) {
            ProgramNode program = new ProgramNode(location(ctx));
            if (ctx.routeDecl() != null) {
                program.addChild(visitRouteDecl(ctx.routeDecl()));
            }
            return program;
        }

        @Override
        public AstNode visitRouteDecl(RouteDeclContext ctx) {
            String path = stripQuotes(ctx.stringLiteral().getText());
            RouteNode route = new RouteNode(location(ctx), path);
            BlockContext block = ctx.block();
            if (block != null) {
                for (BlockItemContext item : block.blockItem()) {
                    if (item.pythonStmt() != null) {
                        AstNode stmt = visit(item.pythonStmt());
                        if (stmt != null) {
                            route.addChild(stmt);
                        }
                    } else if (item.templateDecl() != null) {
                        route.addChild(visitTemplateDecl(item.templateDecl()));
                    }
                }
            }
            return route;
        }

        @Override
        public AstNode visitAssignment(AssignmentContext ctx) {
            Token start = ctx.getStart();
            Token stop = ctx.expression().getStop();
            int startIdx = Math.max(0, start.getStartIndex());
            int stopIdx = Math.max(startIdx, stop.getStopIndex());
            String code = tokens.getTokenSource().getInputStream()
                    .getText(new org.antlr.v4.runtime.misc.Interval(startIdx, stopIdx));
            return new PythonLineNode(location(ctx), code);
        }

        @Override
        public AstNode visitTemplateDecl(TemplateDeclContext ctx) {
            TemplateNode template = new TemplateNode(location(ctx), ctx.IDENT().getText());
            TemplateBodyContext body = ctx.templateBody();
            if (body != null) {
                visitTemplateItems(body.templateItem(), template);
            }
            return template;
        }

        private void visitTemplateItems(List<TemplateItemContext> items, TemplateNode template) {
            for (TemplateItemContext item : items) {
                if (item.templateBlock() != null) {
                    visitTemplateItems(item.templateBlock().templateItem(), template);
                } else if (item.templateLine() != null) {
                    visitTemplateLine(item.templateLine(), template);
                }
            }
        }

        private void visitTemplateLine(TemplateLineContext line, TemplateNode template) {
            if (line.htmlLine() != null) {
                template.addChild(buildHtmlLine(line.htmlLine()));
            } else if (line.jinjaExpr() != null) {
                template.addChild(buildJinjaExpr(line.jinjaExpr()));
            } else if (line.jinjaStmt() != null) {
                template.addChild(buildJinjaStmt(line.jinjaStmt()));
            } else if (line.cssBlock() != null) {
                template.addChild(buildCssBlock(line.cssBlock()));
            }
        }

        private HtmlLineNode buildHtmlLine(HtmlLineContext ctx) {
            String text = captureRaw(ctx.contentLine());
            return new HtmlLineNode(location(ctx), text);
        }

        private JinjaNode buildJinjaExpr(JinjaExprContext ctx) {
            String body = unwrap(ctx.JINJA_EXPR().getText(), "{{", "}}");
            return new JinjaNode(location(ctx), JinjaNode.Kind.EXPR, body);
        }

        private JinjaNode buildJinjaStmt(JinjaStmtContext ctx) {
            String body = unwrap(ctx.JINJA_STMT().getText(), "{%", "%}");
            return new JinjaNode(location(ctx), JinjaNode.Kind.STMT, body);
        }

        private CssBlockNode buildCssBlock(CssBlockContext ctx) {
            CssBlockNode block = new CssBlockNode(location(ctx));
            List<CssLineContext> lines = ctx.cssSuite().cssLine();
            for (CssLineContext line : lines) {
                String text = captureRaw(line.contentLine());
                block.addChild(new CssRuleNode(location(line), text));
            }
            return block;
        }

        private AstNode visit(ParserRuleContext ctx) {
            if (ctx == null) {
                return null;
            }
            return super.visit(ctx);
        }

        private SourceLocation location(ParserRuleContext ctx) {
            Token token = ctx.getStart();
            return new SourceLocation(fileName, token.getLine(), token.getCharPositionInLine() + 1);
        }

        private String stripQuotes(String text) {
            if (text.startsWith("\"") && text.endsWith("\"")) {
                return text.substring(1, text.length() - 1);
            }
            return text;
        }

        private String unwrap(String text, String prefix, String suffix) {
            String value = text.trim();
            if (value.startsWith(prefix)) {
                value = value.substring(prefix.length());
            }
            if (value.endsWith(suffix)) {
                value = value.substring(0, value.length() - suffix.length());
            }
            return value.trim();
        }

        private String captureRaw(ParserRuleContext ctx) {
            if (ctx == null) {
                return "";
            }
            int start = Math.max(0, ctx.getStart().getStartIndex());
            int stop = Math.max(start, ctx.getStop().getStopIndex());
            stop = Math.min(stop, source.length() - 1);
            String snippet = source.substring(start, stop + 1);
            return snippet.replaceAll("[\\r\\n]+$", "");
        }
    }
}
