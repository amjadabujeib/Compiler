// Parser grammar for Jinja templates with embedded HTML/CSS.
parser grammar JinjaHtmlParser;

options { tokenVocab=JinjaHtmlLexer; }

// Example: <p>{{ user.name }}</p>
template
    :   (htmlChunk | styleBlock | jinjaExpr | jinjaStmt)* EOF
    ;

// Example: <h1>Title</h1>
htmlChunk
    :   HTML_TEXT
    ;

// Example: <style>body { color: red; }</style>
styleBlock
    :   STYLE_OPEN cssRule* STYLE_CLOSE
    ;

// Example: {{ user.name }}
jinjaExpr
    :   JINJA_EXPR_START jinjaExpression JINJA_EXPR_END
    ;

// Example: {% if user %}
jinjaStmt
    :   JINJA_STMT_START jinjaStatement JINJA_STMT_END
    ;

// Example: for product in products
jinjaStatement
    :   for_stmt
    |   if_stmt
    |   elif_stmt
    |   else_stmt
    |   endif_stmt
    |   endfor_stmt
    ;

// Example: body { color: red; }
cssRule
    :   cssSelectorList CSS_LBRACE cssDeclaration* CSS_RBRACE
    ;

// Example: body, .product
cssSelectorList
    :   cssSelector (CSS_COMMA cssSelector)*
    ;

// Example: .product img
cssSelector
    :   cssSelectorItem+
    ;

// Example: body
cssSelectorItem
    :   CSS_IDENT
    |   CSS_DOT
    |   CSS_HASH
    |   CSS_NUMBER
    |   CSS_DIMENSION
    ;

// Example: color: red;
cssDeclaration
    :   cssProperty CSS_COLON cssValue? CSS_SEMI
    ;

// Example: color
cssProperty
    :   CSS_IDENT
    ;

// Example: red
cssValue
    :   cssValueItem+
    ;

// Example: 100%
cssValueItem
    :   CSS_DIMENSION
    |   CSS_NUMBER
    |   CSS_IDENT
    |   CSS_HASH
    |   CSS_DOT
    |   CSS_COMMA
    |   CSS_LPAREN
    |   CSS_RPAREN
    |   CSS_STRING
    ;

// Example: for product in products
for_stmt
    :   FOR target_list IN jinjaExpression
    ;

// Example: product, index
target_list
    :   NAME (COMMA NAME)* COMMA?
    ;

// Example: if user
if_stmt
    :   IF jinjaExpression
    ;

// Example: elif user.is_admin
elif_stmt
    :   ELIF jinjaExpression
    ;

// Example: else
else_stmt
    :   ELSE
    ;

// Example: endif
endif_stmt
    :   ENDIF
    ;

// Example: endfor
endfor_stmt
    :   ENDFOR
    ;

// Example: user.name|upper
jinjaExpression
    :   or_test (PIPE jinjaFilter)*
    ;

// Example: upper
jinjaFilter
    :   NAME (LPAREN arglist? RPAREN)?
    ;

// Example: a or b
or_test
    :   and_test (OR and_test)*
    ;

// Example: a and b
and_test
    :   not_test (AND not_test)*
    ;

// Example: not a
not_test
    :   NOT not_test
    |   comparison
    ;

// Example: a == b
comparison
    :   arith_expr (comp_op arith_expr)*
    ;

// Example: ==
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

// Example: a + b - c
arith_expr
    :   term ((PLUS | MINUS) term)*
    ;

// Example: a * b
term
    :   factor ((STAR | SLASH | PERCENT) factor)*
    ;

// Example: -a
factor
    :   (PLUS | MINUS) factor
    |   atom_expr
    ;

// Example: product.price
atom_expr
    :   atom trailer*
    ;

// Example: 'text'
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

// Example: (arg)
trailer
    :   LPAREN arglist? RPAREN
    |   LBRACK subscriptlist RBRACK
    |   DOT NAME
    ;

// Example: a, b, c
list_items
    :   jinjaExpression (COMMA jinjaExpression)* COMMA?
    ;

// Example: 'a': 1, 'b': 2
dict_items
    :   dict_item (COMMA dict_item)* COMMA?
    ;

// Example: 'a': 1
dict_item
    :   jinjaExpression COLON jinjaExpression
    ;

// Example: 0, 1
subscriptlist
    :   subscript (COMMA subscript)* COMMA?
    ;

// Example: 1:3
subscript
    :   jinjaExpression? COLON jinjaExpression? (COLON jinjaExpression?)?
    |   jinjaExpression
    ;

// Example: name='x', price=1
arglist
    :   argument (COMMA argument)* COMMA?
    ;

// Example: name='x'
argument
    :   NAME ASSIGN jinjaExpression
    |   jinjaExpression
    ;
