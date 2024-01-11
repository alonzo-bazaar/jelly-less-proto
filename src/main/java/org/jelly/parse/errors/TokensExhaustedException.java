package org.jelly.parse.errors;
import org.jelly.lang.errors.TokenParsingException;

public class TokensExhaustedException extends TokenParsingException {
    public TokensExhaustedException(String s) {
        super(s);
    }

    public TokensExhaustedException(String s, int r, int c) {
        super(s, r, c);
    }
}
