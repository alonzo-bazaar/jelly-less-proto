package org.jelly.parse.token.errors;

import org.jelly.lang.errors.CompilationError;
public class TokenParsingException extends CompilationError {
    public TokenParsingException(String s) {
        super(s);
    }

    public TokenParsingException(String s, Throwable t) {
        super(s, t);
    }
}
