package eval;

public class VariableDoesNotExistException extends EnvironmentException {
    public VariableDoesNotExistException(String s) {
        super(s);
    }
}
