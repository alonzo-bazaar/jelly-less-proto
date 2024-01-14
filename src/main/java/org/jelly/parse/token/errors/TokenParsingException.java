package org.jelly.parse.token.errors;

public class TokenParsingException extends RuntimeException {
    public final static int noRow = -1;
    private int row=noRow;
    public final static int noCol = -1;
    private int col=noCol;
    public final static String noFile = null;
    private String filename=noFile;

    public TokenParsingException(String s) {
        super(s);
    }

    public TokenParsingException(String s, int row, int col, String filename) {
        super(s + (row!=-1?" at row <" + row + ">" : "")
                + (col!=-1? " at column <" + col + ">" : "")
                + (filename!=null?" in file <" + filename + ">" : ""));
        this.row = row;
        this.col = col;
        this.filename = filename;
    }

    public boolean hasRow() {
        return row != noRow;
    }
    public int getRow() {
        return row;
    }

    public boolean hasCol() {
        return col != noCol;
    }

    public int getCol() {
        return col;
    }

    public boolean hasFilename() {
        return  filename != noFile;
    }

    public String getFilename() {
        return filename;
    }
}
