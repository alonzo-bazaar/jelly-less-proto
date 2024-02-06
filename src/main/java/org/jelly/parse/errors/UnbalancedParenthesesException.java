package org.jelly.parse.errors;
import org.jelly.parse.token.errors.TokenParsingException;

public class UnbalancedParenthesesException extends TokenParsingException {
    public UnbalancedParenthesesException(String s) {
        super(s);
    }
    public UnbalancedParenthesesException(String s, Throwable t) {
        super(s, t);
    }
}
