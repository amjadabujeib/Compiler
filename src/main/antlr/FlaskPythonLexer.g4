// Lexer grammar for the Flask-oriented Python subset.
lexer grammar FlaskPythonLexer;

@members {
    private java.util.Deque<Integer> indents = new java.util.ArrayDeque<>();
    private int opened = 0;
    private java.util.LinkedList<org.antlr.v4.runtime.Token> tokens = new java.util.LinkedList<>();

    @Override
    public void emit(org.antlr.v4.runtime.Token token) {
        super.setToken(token);
        tokens.add(token);
    }

    @Override
    public org.antlr.v4.runtime.Token nextToken() {
        while (true) {
            if (!tokens.isEmpty()) {
                return tokens.poll();
            }
            org.antlr.v4.runtime.Token next = super.nextToken();
            if (next.getType() == EOF) {
                if (!tokens.isEmpty()) {
                    tokens.pollLast();
                }
                while (!indents.isEmpty()) {
                    indents.pop();
                    emit(commonToken(DEDENT, ""));
                }
                emit(next);
            }
        }
    }

    private org.antlr.v4.runtime.Token commonToken(int type, String text) {
        int start = this._tokenStartCharIndex;
        int stop = start + text.length() - 1;
        if (stop < start) {
            stop = start;
        }
        return _factory.create(_tokenFactorySourcePair, type, text,
                org.antlr.v4.runtime.Token.DEFAULT_CHANNEL,
                start,
                stop,
                getLine(),
                getCharPositionInLine());
    }

    private int getIndentationCount(String spaces) {
        int count = 0;
        for (char ch : spaces.toCharArray()) {
            if (ch == '\t') {
                count += 8 - (count % 8);
            } else {
                count++;
            }
        }
        return count;
    }
}

DEF     : 'def';
RETURN  : 'return';
IF      : 'if';
ELIF    : 'elif';
ELSE    : 'else';
TRY     : 'try';
EXCEPT  : 'except';
FINALLY : 'finally';
FOR     : 'for';
IN      : 'in';
FROM    : 'from';
IMPORT  : 'import';
AS      : 'as';
GLOBAL  : 'global';
PASS    : 'pass';
NONE    : 'None';
TRUE    : 'True';
FALSE   : 'False';
AND     : 'and';
OR      : 'or';
NOT     : 'not';
IS      : 'is';

LPAREN  : '(' {opened++;};
RPAREN  : ')' {opened = Math.max(0, opened - 1);};
LBRACK  : '[' {opened++;};
RBRACK  : ']' {opened = Math.max(0, opened - 1);};
LBRACE  : '{' {opened++;};
RBRACE  : '}' {opened = Math.max(0, opened - 1);};

AT      : '@';
COLON   : ':';
COMMA   : ',';
DOT     : '.';
SEMI    : ';';

EQ      : '==';
NE      : '!=';
LE      : '<=';
GE      : '>=';
LT      : '<';
GT      : '>';

PLUS_ASSIGN       : '+=';
MINUS_ASSIGN      : '-=';
STAR_ASSIGN       : '*=';
SLASH_ASSIGN      : '/=';
PERCENT_ASSIGN    : '%=';
DOUBLESLASH_ASSIGN: '//=';
ASSIGN  : '=';

PLUS    : '+';
MINUS   : '-';
STAR    : '*';
SLASH   : '/';
DOUBLESLASH : '//';
PERCENT : '%';

STRING
    :   [fF]? (SHORT_STRING | LONG_STRING)
    ;

NUMBER
    :   DIGIT+ ('.' DIGIT+)?
    ;

NAME
    :   [a-zA-Z_] [a-zA-Z0-9_]*
    ;

NEWLINE
    :   ('\r'? '\n' | '\r' | '\f') [ \t]*
        {
            int nextChar = _input.LA(1);
            if (opened > 0) {
                skip();
            } else {
                emit(commonToken(NEWLINE, "\n"));
                boolean isBlank = nextChar == '\r' || nextChar == '\n' || nextChar == '\f'
                        || nextChar == '#' || nextChar == -1;
                if (!isBlank) {
                    String spaces = getText().replaceAll("[\r\n\f]", "");
                    int indent = getIndentationCount(spaces);
                    int previous = indents.isEmpty() ? 0 : indents.peek();
                    if (indent > previous) {
                        indents.push(indent);
                        emit(commonToken(INDENT, ""));
                    } else {
                        while (!indents.isEmpty() && indents.peek() > indent) {
                            indents.pop();
                            emit(commonToken(DEDENT, ""));
                        }
                    }
                }
            }
        }
        -> skip
    ;

COMMENT : '#' ~[\r\n]* -> skip;
WS      : [ \t]+ -> skip;

INDENT : 'INDENT';
DEDENT : 'DEDENT';

fragment SHORT_STRING
    :   '\'' ( '\\' . | ~['\\\r\n] )* '\''
    |   '"' ( '\\' . | ~["\\\r\n] )* '"'
    ;

fragment LONG_STRING
    :   '\'' '\'' '\'' .*? '\'' '\'' '\''
    |   '"' '"' '"' .*? '"' '"' '"'
    ;

fragment DIGIT : [0-9];
