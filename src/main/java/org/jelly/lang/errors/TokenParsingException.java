package org.jelly.lang.errors;

public class TokenParsingException extends ParsingException {
    public TokenParsingException(String s) {
        super(s);
    }

    public TokenParsingException(String s, int r, int c) {
        super(s,r,c);
    }
}
