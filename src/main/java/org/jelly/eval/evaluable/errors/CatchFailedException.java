package org.jelly.eval.evaluable.errors;

public class CatchFailedException extends RuntimeException {
    public CatchFailedException(String s, Throwable t) {
        super(s, t);
    }
}
