package org.jelly.lang.errors;

public class TextParsingException extends ParsingException {
    public TextParsingException(String s) {
        super(s);
    }

    public TextParsingException(String s, int r, int c) {
        super(s,r,c);
    }
}
