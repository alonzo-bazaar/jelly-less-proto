package org.jelly.eval.errors;

public class ForeignCallException extends RuntimeException {
    public ForeignCallException(String s, Throwable cause) {
        super(s, cause);
    }
}
