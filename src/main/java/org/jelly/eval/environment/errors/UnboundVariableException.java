package org.jelly.eval.environment.errors;

public class UnboundVariableException extends RuntimeException {
    public UnboundVariableException(String s) {
        super(s);
    }
}
