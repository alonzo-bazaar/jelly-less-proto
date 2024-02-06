package org.jelly.parse.token.errors;

import org.jelly.parse.errors.ParsingException;

public class TextParsingException extends ParsingException {
    public TextParsingException(String s) {
        super(s);
    }

    public TextParsingException(String s, Throwable t) {
        super(s,t);
    }
}
