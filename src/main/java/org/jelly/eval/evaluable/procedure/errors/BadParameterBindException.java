package org.jelly.eval.evaluable.procedure.errors;

public class BadParameterBindException extends RuntimeException {
    public BadParameterBindException(String s) {
        super(s);
    }
    public BadParameterBindException(String s, Throwable c) {
        super(s, c);
    }
}
