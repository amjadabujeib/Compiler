package com.compiler.flask.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MiniJsonParser {
    private final String text;
    private int index;

    private MiniJsonParser(String text) {
        this.text = text;
    }

    public static Object parse(String text) {
        return new MiniJsonParser(text).parseValue();
    }

    private Object parseValue() {
        skipWhitespace();
        if (index >= text.length()) {
            throw new IllegalStateException("Unexpected end of input");
        }
        char ch = text.charAt(index);
        if (ch == '{') {
            return parseObject();
        }
        if (ch == '[') {
            return parseArray();
        }
        if (ch == '"') {
            return parseString();
        }
        if (ch == '-' || Character.isDigit(ch)) {
            return parseNumber();
        }
        if (Character.isLetter(ch)) {
            return parseKeyword();
        }
        throw new IllegalStateException("Unsupported JSON token at position " + index);
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> result = new LinkedHashMap<>();
        expect('{');
        skipWhitespace();
        if (peek('}')) {
            index++;
            return result;
        }
        while (index < text.length()) {
            String key = parseString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            Object value = parseValue();
            result.put(key, value);
            skipWhitespace();
            if (peek('}')) {
                index++;
                break;
            }
            expect(',');
            skipWhitespace();
        }
        return result;
    }

    private List<Object> parseArray() {
        List<Object> result = new ArrayList<>();
        expect('[');
        skipWhitespace();
        if (peek(']')) {
            index++;
            return result;
        }
        while (index < text.length()) {
            Object value = parseValue();
            result.add(value);
            skipWhitespace();
            if (peek(']')) {
                index++;
                break;
            }
            expect(',');
            skipWhitespace();
        }
        return result;
    }

    private String parseString() {
        expect('"');
        StringBuilder builder = new StringBuilder();
        while (index < text.length()) {
            char ch = text.charAt(index++);
            if (ch == '"') {
                break;
            }
            if (ch == '\\') {
                if (index >= text.length()) {
                    break;
                }
                char escape = text.charAt(index++);
                switch (escape) {
                    case '"':
                    case '\\':
                    case '/':
                        builder.append(escape);
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    default:
                        builder.append(escape);
                        break;
                }
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    private Number parseNumber() {
        int start = index;
        if (text.charAt(index) == '-') {
            index++;
        }
        while (index < text.length() && Character.isDigit(text.charAt(index))) {
            index++;
        }
        if (index < text.length() && text.charAt(index) == '.') {
            index++;
            while (index < text.length() && Character.isDigit(text.charAt(index))) {
                index++;
            }
        }
        String number = text.substring(start, index);
        if (number.contains(".")) {
            return Double.parseDouble(number);
        }
        return Long.parseLong(number);
    }

    private Object parseKeyword() {
        int start = index;
        while (index < text.length() && Character.isLetter(text.charAt(index))) {
            index++;
        }
        String keyword = text.substring(start, index);
        return switch (keyword) {
            case "true" -> Boolean.TRUE;
            case "false" -> Boolean.FALSE;
            case "null" -> null;
            default -> throw new IllegalStateException("Unsupported keyword '" + keyword + "' at position " + start);
        };
    }

    private void expect(char expected) {
        if (index >= text.length() || text.charAt(index) != expected) {
            throw new IllegalStateException("Expected '" + expected + "' at position " + index);
        }
        index++;
    }

    private boolean peek(char candidate) {
        return index < text.length() && text.charAt(index) == candidate;
    }

    private void skipWhitespace() {
        while (index < text.length()) {
            char ch = text.charAt(index);
            if (!Character.isWhitespace(ch)) {
                break;
            }
            index++;
        }
    }
}
