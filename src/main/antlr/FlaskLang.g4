grammar FlaskLang;



@lexer::members {
    private java.util.Deque<Integer> indents = new java.util.ArrayDeque<>();

    private int opened = 0;

    private java.util.LinkedList<org.antlr.v4.runtime.Token> tokens = new java.util.LinkedList<>();

    private boolean pendingTemplateIndent = false;

    private boolean inTemplate = false;
    private int templateBaseIndent = -1;

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
                inTemplate = false;
                templateBaseIndent = -1;
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

    private boolean insideTemplate() {
        return inTemplate;
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


program
    :   NEWLINE* routeDecl NEWLINE* EOF
    ;


routeDecl
    :   ROUTE stringLiteral COLON NEWLINE block
    ;

block
    :   INDENT blockItem* DEDENT
    ;

blockItem
    :   pythonStmt
    |   templateDecl
    |   NEWLINE
    ;

pythonStmt
    :   assignment
    ;

assignment
    :   IDENT ASSIGN expression NEWLINE
    ;

templateDecl
    :   TEMPLATE IDENT COLON NEWLINE templateBody
    ;

templateBody
    :   INDENT templateItem* DEDENT
    ;

templateItem
    :   templateBlock
    |   templateLine
    ;

templateBlock
    :   INDENT templateItem* DEDENT
    ;

templateLine
    :   cssBlock
    |   jinjaExpr
    |   jinjaStmt
    |   htmlLine
    |   NEWLINE
    ;

cssBlock
    :   CSS COLON NEWLINE cssSuite
    ;


cssSuite
    :   INDENT cssLine* DEDENT
    ;

cssLine
    :   contentLine
    ;

htmlLine
    :   contentLine
    ;

contentLine
    :   (~(INDENT|DEDENT|NEWLINE))+ NEWLINE
    ;

jinjaExpr
    :   JINJA_EXPR NEWLINE?
    ;

jinjaStmt
    :   JINJA_STMT NEWLINE?
    ;

expression
    :   jsonLiteral
    |   IDENT
    |   stringLiteral
    |   numberLiteral
    ;

jsonLiteral
    :   objectLiteral
    |   arrayLiteral
    ;

objectLiteral
    :   LBRACE (pair (COMMA pair)*)? RBRACE
    ;

pair
    :   stringLiteral COLON expression
    ;

arrayLiteral
    :   LBRACKET (expression (COMMA expression)*)? RBRACKET
    ;

stringLiteral
    :   STRING
    ;

numberLiteral
    :   NUMBER
    ;

ROUTE    : 'route';
TEMPLATE : 'template' { pendingTemplateIndent = true; };
CSS      : 'css';

JINJA_EXPR : '{{' .*? '}}';
JINJA_STMT : '{%' .*? '%}';

LBRACE   : '{' {if (!insideTemplate()) opened++;};
RBRACE   : '}' {if (!insideTemplate()) opened = Math.max(0, opened - 1);};
LBRACKET : '[' {if (!insideTemplate()) opened++;};
RBRACKET : ']' {if (!insideTemplate()) opened = Math.max(0, opened - 1);};
COMMA    : ',';
COLON    : ':';
DOT      : '.';
ASSIGN   : '=';
LT       : '<';
GT       : '>';
SLASH    : '/';
SEMI     : ';';

STRING
    :   '"' ( '\\' . | ~["\\\r\n] )* '"'
    ;

NUMBER
    :   DIGIT+ ('.' DIGIT+)?
    ;

IDENT
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
                boolean isBlank = nextChar == '\r' || nextChar == '\n' || nextChar == '\f' || nextChar == '#' || nextChar == -1;
                if (!isBlank) {
                    String spaces = getText().replaceAll("[\r\n\f]", "");
                    int indent = getIndentationCount(spaces);
                    int previous = indents.isEmpty() ? 0 : indents.peek();
                    if (indent > previous) {
                        indents.push(indent);
                        if (pendingTemplateIndent) {
                            templateBaseIndent = indent;
                            inTemplate = true;
                            pendingTemplateIndent = false;
                        } else {
                            pendingTemplateIndent = false;
                        }
                        emit(commonToken(INDENT, ""));
                    } else {
                        while (!indents.isEmpty() && indents.peek() > indent) {
                            indents.pop();
                            emit(commonToken(DEDENT, ""));
                        }
                        if (inTemplate && indent < templateBaseIndent) {
                            inTemplate = false;
                            templateBaseIndent = -1;
                        }
                    }
                }
            }
        }
        -> skip
    ;

SKIP_SPACE : [ \t]+ -> skip;
COMMENT    : '#' ~[\r\n]* -> skip;

INDENT : 'INDENT';
DEDENT : 'DEDENT';
fragment DIGIT : [0-9];

ANY_CHAR : . ;
