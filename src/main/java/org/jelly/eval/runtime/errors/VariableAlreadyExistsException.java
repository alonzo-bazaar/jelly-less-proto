package org.jelly.eval.runtime.errors;

public class VariableAlreadyExistsException extends EnvironmentException {
    public VariableAlreadyExistsException(String s) {
        super(s);
    }
}
