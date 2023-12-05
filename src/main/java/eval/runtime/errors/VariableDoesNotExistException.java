package eval.runtime.errors;

public class VariableDoesNotExistException extends EnvironmentException {
    public VariableDoesNotExistException(String s) {
        super(s);
    }
}
