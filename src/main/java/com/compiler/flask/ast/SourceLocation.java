// Source file position (file/line/column) value object.
package com.compiler.flask.ast;

import java.util.Objects;

public final class SourceLocation {
    private final String file;
    private final int line;
    private final int column;

    public SourceLocation(String file, int line, int column) {
        this.file = file;
        this.line = line;
        this.column = column;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    @Override
    public String toString() {
        return file + ":" + line + ":" + column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SourceLocation that)) {
            return false;
        }
        return line == that.line && column == that.column && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, line, column);
    }
}
