package parse;

public class UnbalancedParensException extends ParsingException {
    public UnbalancedParensException(String s) {
        super(s,0,0); // valor placeholder, da aggiornare
    }
}
