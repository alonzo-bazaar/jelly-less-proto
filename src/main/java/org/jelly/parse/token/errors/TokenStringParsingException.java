package org.jelly.parse.token.errors;

import org.jelly.lang.errors.CompilationError;

public class TokenStringParsingException extends CompilationError {
    public TokenStringParsingException(String s) {
        super(s);
    }

    public TokenStringParsingException(String s, Throwable t) {
        super(s, t);
    }
}
