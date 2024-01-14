package org.jelly.parse.token.errors;

public class TokenStringParsingException extends Exception {
    private int columnNumber = TokenParsingException.noCol;
    /* this is supposed to  be thrown by a LineTokenizer
     * this means that it will not be thrown knowing the absolute row position in the file/repl
     * but it will only be able to keep track of how many rows deep it is
     * so we store a row OFFSET and not a row NUMBER
     */
    private int rowOffset = TokenParsingException.noRow;

    public TokenStringParsingException(String s) {
        super(s);
    }

    public TokenStringParsingException(String s, int row, int col) {
        super(s);
        this.rowOffset = row;
        this.columnNumber = col;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public int getRowOffset() {
        return rowOffset;
    }
}
