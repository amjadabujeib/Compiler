parser grammar JinjaHtmlParser;

options { tokenVocab=JinjaHtmlLexer; }

template
    :   (htmlChunk | jinjaExpr | jinjaStmt)* EOF
    ;

htmlChunk
    :   HTML_TEXT
    ;

jinjaExpr
    :   JINJA_EXPR_START jinjaExpression JINJA_EXPR_END
    ;

jinjaStmt
    :   JINJA_STMT_START jinjaStatement JINJA_STMT_END
    ;

jinjaStatement
    :   for_stmt
    |   if_stmt
    |   elif_stmt
    |   else_stmt
    |   endif_stmt
    |   endfor_stmt
    ;

for_stmt
    :   FOR target_list IN jinjaExpression
    ;

target_list
    :   NAME (COMMA NAME)* COMMA?
    ;

if_stmt
    :   IF jinjaExpression
    ;

elif_stmt
    :   ELIF jinjaExpression
    ;

else_stmt
    :   ELSE
    ;

endif_stmt
    :   ENDIF
    ;

endfor_stmt
    :   ENDFOR
    ;

jinjaExpression
    :   or_test (PIPE jinjaFilter)*
    ;

jinjaFilter
    :   NAME (LPAREN arglist? RPAREN)?
    ;

or_test
    :   and_test (OR and_test)*
    ;

and_test
    :   not_test (AND not_test)*
    ;

not_test
    :   NOT not_test
    |   comparison
    ;

comparison
    :   arith_expr (comp_op arith_expr)*
    ;

comp_op
    :   EQ
    |   NE
    |   LT
    |   GT
    |   LE
    |   GE
    |   IS NOT?
    |   IN
    |   NOT IN
    ;

arith_expr
    :   term ((PLUS | MINUS) term)*
    ;

term
    :   factor ((STAR | SLASH | PERCENT) factor)*
    ;

factor
    :   (PLUS | MINUS) factor
    |   atom_expr
    ;

atom_expr
    :   atom trailer*
    ;

atom
    :   LPAREN jinjaExpression? RPAREN
    |   LBRACK list_items? RBRACK
    |   LBRACE dict_items? RBRACE
    |   NAME
    |   NUMBER
    |   STRING
    |   TRUE
    |   FALSE
    |   NONE
    ;

trailer
    :   LPAREN arglist? RPAREN
    |   LBRACK subscriptlist RBRACK
    |   DOT NAME
    ;

list_items
    :   jinjaExpression (COMMA jinjaExpression)* COMMA?
    ;

dict_items
    :   dict_item (COMMA dict_item)* COMMA?
    ;

dict_item
    :   jinjaExpression COLON jinjaExpression
    ;

subscriptlist
    :   subscript (COMMA subscript)* COMMA?
    ;

subscript
    :   jinjaExpression? COLON jinjaExpression? (COLON jinjaExpression?)?
    |   jinjaExpression
    ;

arglist
    :   argument (COMMA argument)* COMMA?
    ;

argument
    :   NAME ASSIGN jinjaExpression
    |   jinjaExpression
    ;
