package org.jelly.lang.errors;

import org.jelly.parse.errors.ParsingException;

public class TokenParsingException extends ParsingException {
    public TokenParsingException(String s) {
        super(s);
    }

    public TokenParsingException(String s, Throwable t) {
        super(s, t);
    }
}
