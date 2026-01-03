# FlaskCompiler

ANTLR4 grammars that parse a small Flask-focused subset of Python (enough for `app.py`) and Jinja2 templates with embedded HTML/CSS.

## Requirements

- Java 17 (the Gradle build uses a Java 17 toolchain)

## Build

```bash
./gradlew build
```

## Parse (app + templates)

```bash
./gradlew run --args="src/main/resources/tests/app.txt src/main/resources/tests/templates"
```

This validates parsing only and prints the Python AST, Jinja ASTs, and symbol tables (no HTML generation).

Test fixtures live in `src/main/resources/tests`.

Parse a single template (still uses the same app file):

```bash
./gradlew run --args="src/main/resources/tests/app.txt src/main/resources/tests/templates/add_product.txt"
./gradlew run --args="src/main/resources/tests/app.txt src/main/resources/tests/templates/show_products.txt"
./gradlew run --args="src/main/resources/tests/app.txt src/main/resources/tests/templates/product_details.txt"
```

Parse only the Python file:

```bash
./gradlew run --args="--python-only src/main/resources/tests/app.txt"
```

## Grammar coverage (subset)

Python:
- `from ... import ...`, decorators, function defs
- `if/elif/else`, `try/except`
- assignments/augassign, `return`, `global`, expression statements
- literals (strings including f-strings, numbers, lists, dicts)
- calls, attribute access, subscripts, list comps, generator expressions

Templates:
- HTML/CSS text outside Jinja tags
- Jinja statements: `for`, `if`, `elif`, `else`, `endif`, `endfor`
- Jinja expressions: filters, calls, attributes, subscripts/slices
