package com.compiler.flask.core;

import com.compiler.flask.ast.ProgramNode;
import com.compiler.flask.ast.PythonLineNode;
import com.compiler.flask.visitor.BaseAstVisitor;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScriptContextExtractor extends BaseAstVisitor<Void> {
    private final RenderContext context = new RenderContext();
    private final StringBuilder literalBuilder = new StringBuilder();
    private boolean capturingLiteral;
    private LiteralType literalType;
    private String targetName;
    private int depth;

    public RenderContext extract(ProgramNode program) {
        program.accept(this);
        return context;
    }

    @Override
    public Void visitPythonLine(PythonLineNode node) {
        String trimmed = node.getCode().trim();
        if (!capturingLiteral) {
            Assignment assignment = Assignment.tryParse(trimmed);
            if (assignment != null) {
                startCapture(assignment);
            }
        } else {
            literalBuilder.append(trimmed).append('\n');
            depth += delta(trimmed);
            if (depth <= 0) {
                finishCapture();
            }
        }
        return null;
    }

    private void startCapture(Assignment assignment) {
        capturingLiteral = true;
        literalType = assignment.type();
        targetName = assignment.name();
        literalBuilder.setLength(0);
        literalBuilder.append(assignment.value()).append('\n');
        depth = delta(assignment.value());
        if (depth <= 0) {
            finishCapture();
        }
    }

    private void finishCapture() {
        capturingLiteral = false;
        String literal = literalBuilder.toString();
        Object data = MiniJsonParser.parse(literal);
        if (literalType == LiteralType.LIST && data instanceof List<?> list) {
            context.setVariable(targetName, list);
        } else if (literalType == LiteralType.OBJECT && data instanceof Map<?, ?> map) {
            context.setVariable(targetName, map);
        }
        literalBuilder.setLength(0);
        targetName = null;
        literalType = null;
        depth = 0;
    }

    private int delta(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '[' || ch == '{') {
                count++;
            } else if (ch == ']' || ch == '}') {
                count--;
            }
        }
        return count;
    }

    private enum LiteralType {
        LIST,
        OBJECT
    }

    private record Assignment(String name, String value, LiteralType type) {
        private static final Pattern PATTERN = Pattern.compile("^(?<name>[a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*(?<value>.+)$", Pattern.DOTALL);

        static Assignment tryParse(String line) {
            Matcher matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                return null;
            }
            String name = matcher.group("name");
            String value = matcher.group("value").trim();
            if (value.startsWith("[")) {
                return new Assignment(name, value, LiteralType.LIST);
            }
            if (value.startsWith("{")) {
                return new Assignment(name, value, LiteralType.OBJECT);
            }
            return null;
        }
    }
}
