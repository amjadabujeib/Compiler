parser grammar FlaskPythonParser;

options { tokenVocab=FlaskPythonLexer; }

file_input
    :   (stmt | NEWLINE)* EOF
    ;

stmt
    :   simple_stmt
    |   compound_stmt
    ;

simple_stmt
    :   small_stmt NEWLINE
    ;

small_stmt
    :   expr_stmt
    |   return_stmt
    |   import_stmt
    |   global_stmt
    |   pass_stmt
    ;

import_stmt
    :   from_import_stmt
    |   import_name
    ;

from_import_stmt
    :   FROM dotted_name IMPORT import_as_names
    ;

import_name
    :   IMPORT dotted_name (COMMA dotted_name)*
    ;

import_as_names
    :   import_as_name (COMMA import_as_name)*
    ;

import_as_name
    :   NAME (AS NAME)?
    ;

global_stmt
    :   GLOBAL NAME (COMMA NAME)*
    ;

pass_stmt
    :   PASS
    ;

return_stmt
    :   RETURN testlist?
    ;

expr_stmt
    :   testlist (augassign testlist | (ASSIGN testlist)+)?
    ;

augassign
    :   PLUS_ASSIGN
    |   MINUS_ASSIGN
    |   STAR_ASSIGN
    |   SLASH_ASSIGN
    |   PERCENT_ASSIGN
    |   DOUBLESLASH_ASSIGN
    ;

compound_stmt
    :   if_stmt
    |   funcdef
    |   try_stmt
    ;

if_stmt
    :   IF test COLON suite (ELIF test COLON suite)* (ELSE COLON suite)?
    ;

try_stmt
    :   TRY COLON suite (except_clause suite)+ (FINALLY COLON suite)?
    ;

except_clause
    :   EXCEPT (test (AS NAME)?)? COLON
    ;

funcdef
    :   decorators? DEF NAME parameters COLON suite
    ;

decorators
    :   decorator+
    ;

decorator
    :   AT dotted_name (LPAREN arglist? RPAREN)? NEWLINE
    ;

parameters
    :   LPAREN paramlist? RPAREN
    ;

paramlist
    :   NAME (COMMA NAME)* COMMA?
    ;

suite
    :   NEWLINE INDENT (stmt | NEWLINE)+ DEDENT
    ;

testlist
    :   test (COMMA test)* COMMA?
    ;

test
    :   or_test (IF or_test ELSE test)?
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
    :   expr (comp_op expr)*
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

expr
    :   arith_expr
    ;

arith_expr
    :   term ((PLUS | MINUS) term)*
    ;

term
    :   factor ((STAR | SLASH | DOUBLESLASH | PERCENT) factor)*
    ;

factor
    :   (PLUS | MINUS) factor
    |   atom_expr
    ;

atom_expr
    :   atom trailer*
    ;

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

trailer
    :   LPAREN arglist? RPAREN
    |   LBRACK subscriptlist RBRACK
    |   DOT NAME
    ;

subscriptlist
    :   subscript (COMMA subscript)* COMMA?
    ;

subscript
    :   test? COLON test? (COLON test?)?
    |   test
    ;

testlist_comp
    :   test (comp_for | (COMMA test)* COMMA?)
    ;

comp_for
    :   FOR exprlist IN or_test comp_if*
    ;

comp_if
    :   IF or_test
    ;

exprlist
    :   expr (COMMA expr)* COMMA?
    ;

dictorsetmaker
    :   dict_item (COMMA dict_item)* COMMA?
    ;

dict_item
    :   test COLON test
    ;

arglist
    :   argument (COMMA argument)* COMMA?
    ;

argument
    :   test (ASSIGN test)?
    ;

dotted_name
    :   NAME (DOT NAME)*
    ;
