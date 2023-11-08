package parse;

public class TokensExhaustedException extends ParsingException {
    public TokensExhaustedException(String s) {
        super(s,0,0); // valor placeholder, da aggiornare
    }
}
