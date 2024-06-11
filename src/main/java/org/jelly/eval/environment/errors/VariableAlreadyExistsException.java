package org.jelly.eval.environment.errors;

public class VariableAlreadyExistsException extends EnvironmentException {
    public VariableAlreadyExistsException(String s) {
        super(s);
    }
}
