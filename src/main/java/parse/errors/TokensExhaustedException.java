package parse.errors;
import lang.errors.TokenParsingException;

public class TokensExhaustedException extends TokenParsingException {
    public TokensExhaustedException(String s) {
        super(s);
    }

    public TokensExhaustedException(String s, int r, int c) {
        super(s, r, c);
    }
}
