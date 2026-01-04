// Lexer grammar for Jinja templates with HTML/CSS and Jinja tags.
lexer grammar JinjaHtmlLexer;

@members {
    private boolean isStyleStart() {
        if (_input.LA(1) != '<') {
            return false;
        }
        return (_input.LA(2) == 's' || _input.LA(2) == 'S')
            && (_input.LA(3) == 't' || _input.LA(3) == 'T')
            && (_input.LA(4) == 'y' || _input.LA(4) == 'Y')
            && (_input.LA(5) == 'l' || _input.LA(5) == 'L')
            && (_input.LA(6) == 'e' || _input.LA(6) == 'E');
    }
}

JINJA_EXPR_START    : '{{' -> pushMode(JINJA);
JINJA_STMT_START    : '{%' -> pushMode(JINJA);
JINJA_COMMENT_START : '{#' -> pushMode(JINJA_COMMENT);

STYLE_OPEN : '<style' ~[>]* '>' -> pushMode(CSS);

HTML_TEXT
    :   ( ~[<{]
        | '{' ~[{%#]
        | '<' { !isStyleStart() }?
        )+
    ;

mode JINJA;

JINJA_EXPR_END : '}}' -> popMode;
JINJA_STMT_END : '%}' -> popMode;

FOR     : 'for';
IN      : 'in';
IF      : 'if';
ELIF    : 'elif';
ELSE    : 'else';
ENDIF   : 'endif';
ENDFOR  : 'endfor';
TRUE    : 'true';
FALSE   : 'false';
NONE    : 'none';
AND     : 'and';
OR      : 'or';
NOT     : 'not';
IS      : 'is';

EQ      : '==';
NE      : '!=';
LE      : '<=';
GE      : '>=';
LT      : '<';
GT      : '>';
ASSIGN  : '=';
PLUS    : '+';
MINUS   : '-';
STAR    : '*';
SLASH   : '/';
PERCENT : '%';
PIPE    : '|';
COLON   : ':';
COMMA   : ',';
DOT     : '.';
LPAREN  : '(';
RPAREN  : ')';
LBRACK  : '[';
RBRACK  : ']';
LBRACE  : '{';
RBRACE  : '}';

STRING
    :   '\'' ( '\\' . | ~['\\\r\n] )* '\''
    |   '"' ( '\\' . | ~["\\\r\n] )* '"'
    ;

NUMBER
    :   DIGIT+ ('.' DIGIT+)?
    ;

NAME
    :   [a-zA-Z_] [a-zA-Z0-9_]*
    ;

WS : [ \t\r\n]+ -> skip;

mode JINJA_COMMENT;

JINJA_COMMENT_END : '#}' -> popMode, skip;
JINJA_COMMENT_TEXT : . -> skip;

mode CSS;

STYLE_CLOSE : '</style>' -> popMode;

CSS_LBRACE : '{';
CSS_RBRACE : '}';
CSS_COLON : ':';
CSS_SEMI : ';';
CSS_COMMA : ',';
CSS_DOT : '.';
CSS_LPAREN : '(';
CSS_RPAREN : ')';
CSS_HASH : '#' [a-zA-Z0-9_-]+;
CSS_DIMENSION : DIGIT+ ('.' DIGIT+)? [a-zA-Z%]+;
CSS_NUMBER : DIGIT+ ('.' DIGIT+)?;
CSS_STRING
    :   '\'' ( '\\' . | ~['\\\r\n] )* '\''
    |   '"' ( '\\' . | ~["\\\r\n] )* '"'
    ;
CSS_IDENT : [a-zA-Z_-] [a-zA-Z0-9_-]*;
CSS_WS : [ \t\r\n]+ -> skip;

fragment DIGIT : [0-9];
