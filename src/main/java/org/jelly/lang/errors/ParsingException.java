package org.jelly.lang.errors;

public class ParsingException extends CompilationError {
    public ParsingException(String s) {
        super(s);
    }

    public ParsingException(String s, int r, int c) {
        super(s,r,c);
    }
}
