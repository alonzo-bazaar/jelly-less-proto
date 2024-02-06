package org.jelly.parse.errors;
import org.jelly.parse.token.errors.TokenParsingException;

public class TokensExhaustedException extends TokenParsingException {
    public TokensExhaustedException(String s) {
        super(s);
    }

    public TokensExhaustedException(String s, Throwable t) {
        super(s, t);
    }
}
