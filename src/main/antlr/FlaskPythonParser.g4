// Parser grammar for the Flask-oriented Python subset.
parser grammar FlaskPythonParser;

options { tokenVocab=FlaskPythonLexer; }

// Example: from flask import Flask
file_input
    :   (stmt | NEWLINE)* EOF
    ;

// Example: app = Flask(__name__)
stmt
    :   simple_stmt
    |   compound_stmt
    ;

// Example: return value
simple_stmt
    :   small_stmt NEWLINE
    ;

// Example: pass
small_stmt
    :   expr_stmt
    |   return_stmt
    |   import_stmt
    |   global_stmt
    |   pass_stmt
    ;

// Example: from flask import Flask
import_stmt
    :   from_import_stmt
    |   import_name
    ;

// Example: from flask import Flask, render_template
from_import_stmt
    :   FROM dotted_name IMPORT import_as_names
    ;

// Example: import os, sys
import_name
    :   IMPORT dotted_name (COMMA dotted_name)*
    ;

// Example: Flask, render_template
import_as_names
    :   import_as_name (COMMA import_as_name)*
    ;

// Example: render_template as rt
import_as_name
    :   NAME (AS NAME)?
    ;

// Example: global next_id
global_stmt
    :   GLOBAL NAME (COMMA NAME)*
    ;

// Example: pass
pass_stmt
    :   PASS
    ;

// Example: return redirect(url_for('show_products'))
return_stmt
    :   RETURN testlist?
    ;

// Example: x = y
expr_stmt
    :   testlist (augassign testlist | (ASSIGN testlist)+)?
    ;

// Example: x += 1
augassign
    :   PLUS_ASSIGN
    |   MINUS_ASSIGN
    |   STAR_ASSIGN
    |   SLASH_ASSIGN
    |   PERCENT_ASSIGN
    |   DOUBLESLASH_ASSIGN
    ;

// Example: if x: ...
compound_stmt
    :   if_stmt
    |   funcdef
    |   try_stmt
    ;

// Example: if x: return y
if_stmt
    :   IF test COLON suite (ELIF test COLON suite)* (ELSE COLON suite)?
    ;

// Example: try: ... except ValueError: ...
try_stmt
    :   TRY COLON suite (except_clause suite)+ (FINALLY COLON suite)?
    ;

// Example: except ValueError as e:
except_clause
    :   EXCEPT (test (AS NAME)?)? COLON
    ;

// Example: def add_product(): ...
funcdef
    :   decorators? DEF NAME parameters COLON suite
    ;

// Example: @app.route('/')
decorators
    :   decorator+
    ;

// Example: @app.route('/products')
decorator
    :   AT dotted_name (LPAREN arglist? RPAREN)? NEWLINE
    ;

// Example: (product_id)
parameters
    :   LPAREN paramlist? RPAREN
    ;

// Example: a, b
paramlist
    :   NAME (COMMA NAME)* COMMA?
    ;

// Example: NEWLINE INDENT stmt DEDENT
suite
    :   NEWLINE INDENT (stmt | NEWLINE)+ DEDENT
    ;

// Example: a, b
testlist
    :   test (COMMA test)* COMMA?
    ;

// Example: a if cond else b
test
    :   or_test (IF or_test ELSE test)?
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
    :   expr (comp_op expr)*
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

// Example: a + b
expr
    :   arith_expr
    ;

// Example: a + b - c
arith_expr
    :   term ((PLUS | MINUS) term)*
    ;

// Example: a * b
term
    :   factor ((STAR | SLASH | DOUBLESLASH | PERCENT) factor)*
    ;

// Example: -x
factor
    :   (PLUS | MINUS) factor
    |   atom_expr
    ;

// Example: request.form.get('name')
atom_expr
    :   atom trailer*
    ;

// Example: 'text'
atom
    :   LPAREN testlist_comp? RPAREN
    |   LBRACK testlist_comp? RBRACK
    |   LBRACE dictorsetmaker? RBRACE
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

// Example: 0, 1
subscriptlist
    :   subscript (COMMA subscript)* COMMA?
    ;

// Example: 1:3
subscript
    :   test? COLON test? (COLON test?)?
    |   test
    ;

// Example: x for x in items
testlist_comp
    :   test (comp_for | (COMMA test)* COMMA?)
    ;

// Example: for x in items
comp_for
    :   FOR exprlist IN or_test comp_if*
    ;

// Example: if x > 0
comp_if
    :   IF or_test
    ;

// Example: x, y
exprlist
    :   expr (COMMA expr)* COMMA?
    ;

// Example: {'id': 1, 'name': 'x'}
dictorsetmaker
    :   dict_item (COMMA dict_item)* COMMA?
    ;

// Example: 'id': 1
dict_item
    :   test COLON test
    ;

// Example: name='x', price=1
arglist
    :   argument (COMMA argument)* COMMA?
    ;

// Example: name='x'
argument
    :   test (ASSIGN test)?
    ;

// Example: flask.render_template
dotted_name
    :   NAME (DOT NAME)*
    ;
