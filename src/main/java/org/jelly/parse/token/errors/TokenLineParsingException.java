package org.jelly.parse.token.errors;

public class TokenLineParsingException extends Exception {
    private int columnNumber = TokenParsingException.noCol;

    public TokenLineParsingException(String s) {
        super(s);
    }

    public TokenLineParsingException(String s, int col) {
        super(s);
        this.columnNumber = col;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}
