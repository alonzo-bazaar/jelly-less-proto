package org.jelly.eval.environment.errors;

public class VariableDoesNotExistException extends EnvironmentException {
    public VariableDoesNotExistException(String s) {
        super(s);
    }
}
