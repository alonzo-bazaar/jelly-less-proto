package eval;

public class VariableAlreadyExistsException extends EnvironmentException {
    public VariableAlreadyExistsException(String s) {
        super(s);
    }
}
