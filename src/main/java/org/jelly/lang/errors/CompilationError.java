package org.jelly.lang.errors;

public class CompilationError extends RuntimeException {
    private final int row;
    private final int column;
    public CompilationError(String s) {
        super(s);
        this.row = -1;
        this.column = -1;
    }

    public CompilationError(String s, int row, int column) {
        super(s);
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }
}
