# FlaskCompiler

A tiny Java + ANTLR4 compiler for an indentation-sensitive DSL (similar to python) that renders HTML pages with a minimal Jinja-like templating layer.

## Requirements

- Java 17 (the Gradle build uses a Java 17 toolchain)

## Build

```bash
./gradlew build
```

## Run (compile a script to HTML)

`Main` supports:

```bash
./gradlew run --args="<script> <outDir>"
```

Example:

```bash
./gradlew run --args="src/main/resources/tests/show_products.txt out"
# writes: out/show_products.html and copies assets to out/assets/
```

## Run (serve the sample app)

Start the built-in HTTP server:

```bash
./gradlew run --args="--serve 8080"
```

Then open:

- `http://localhost:8080/products`
- `http://localhost:8080/products/add`
- `http://localhost:8080/products/detail?name=iPhone%2015`

## Language overview (FlaskLang)

FlaskLang is whitespace/indentation sensitive (similar to Python).

### Top-level

- A script contains a single `route`:

```text
route "/path":
    ...
```

### Variables

- Inside a route you can assign JSON-like lists/objects to names; these become template variables:

```text
products = [
    { "name": "iPhone 15", "price": 799.00 }
]
```

### Templates

- Define a template block and write raw HTML lines inside it:

```text
template page:
    <h1>Hello</h1>
```

### Templating (supported subset)

- Expression substitution: `{{ variable }}` with dot access into objects/maps (e.g. `{{ product.name }}`).
- Simple loops: `{% for item in items %}` ... `{% endfor %}` (nested `for` loops are not supported).

### CSS blocks

- Inside a template, a `css:` block collects CSS rules and emits them into a `<style>` tag:

```text
css:
    .card { border-radius: 12px; }
```

## Handy dev tasks

The Gradle build includes a few parse-tree viewer tasks for the sample scripts:

```bash
./gradlew showProductsParseTree
./gradlew addProductParseTree
./gradlew showProductDetailsParseTree
```

