package parse;

public class ParsingException extends Exception {
    private final int row;
    private final int column;
    public ParsingException(String s, int row, int column) {
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
