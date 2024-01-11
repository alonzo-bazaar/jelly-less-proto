package org.jelly.parse.errors;
import org.jelly.lang.errors.TokenParsingException;

public class UnbalancedParenthesesException extends TokenParsingException {
    public UnbalancedParenthesesException(String s) {
        super(s);
    }

    public UnbalancedParenthesesException(String s, int r, int c) {
        super(s, r, c);
    }
}
