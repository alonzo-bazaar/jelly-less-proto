package org.jelly.parse.errors;

public class SyntaxTreeParsingException extends ParsingException {
    public SyntaxTreeParsingException(String s) {
        super(s);
    }
    public SyntaxTreeParsingException(String s, Throwable t) {
        super(s, t);
    }
}
