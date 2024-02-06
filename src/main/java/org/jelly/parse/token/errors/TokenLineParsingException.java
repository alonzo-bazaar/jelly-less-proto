package org.jelly.parse.token.errors;

import org.jelly.lang.errors.CompilationError;
import org.jelly.lang.errors.TokenParsingException;

public class TokenLineParsingException extends RuntimeException {

    private static final int unassigned = -1;
    private int column = unassigned;
    private int rowOffset = unassigned;
    public TokenLineParsingException(String s) {
        super(s);
    }

    public TokenLineParsingException(String s, Throwable t) {
        super(s, t);
    }

    public TokenLineParsingException setColumn(int c) {
        this.column = c;
        return this;
    }

    public TokenLineParsingException setRowOffset(int off) {
        this.rowOffset = off;
        return this;
    }

    public int getColumn() {
        return column;
    }

    public int getRowOffset() {
        return rowOffset;
    }
}
