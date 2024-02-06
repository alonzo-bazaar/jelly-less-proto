package org.jelly.lang.errors;

public class CompilationError extends RuntimeException {
    private int row;
    private int column;

    private String filename;

    private static final int unassigned = -1; // to avoid any "magic numbers"

    public CompilationError(String s) {
        super(s);
        this.row = unassigned;
        this.column = unassigned;
        this.filename = null;
    }

    public CompilationError (String s, Throwable t) {
        super(s, t);
        this.row = unassigned;
        this.column = unassigned;
    }

    @Override
    public String getMessage() {
        return super.getMessage() +
                (hasRow()?(" in row [" + row + "] "):"") +
                (hasColumn()?(" in column [" + column + "] "):"") +
                (hasFilename()?("in file [" + filename + "] "):"");
    }

    // telescopic constructor if you really want to set the row and column of the exception
    public CompilationError setRow(int r) {
        this.row = r;
        return this;
    }

    public CompilationError setColumn(int c) {
        this.column = c;
        return this;
    }

    public CompilationError setFilename(String fn) {
        this.filename = fn;
        return this;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public boolean hasRow() {
        return this.row != unassigned;
    }

    public boolean hasColumn() {
        return this.column != unassigned;
    }

    public boolean hasFilename() {
        return this.filename != null;
    }
}
