lexer grammar JinjaHtmlLexer;

JINJA_EXPR_START    : '{{' -> pushMode(JINJA);
JINJA_STMT_START    : '{%' -> pushMode(JINJA);
JINJA_COMMENT_START : '{#' -> pushMode(JINJA_COMMENT);

HTML_TEXT
    :   ( ~'{' | '{' ~[{%#] )+
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

fragment DIGIT : [0-9];
