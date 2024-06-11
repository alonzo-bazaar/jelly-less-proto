package org.jelly.eval.evaluable.procedure.errors;

public class BadCallException extends RuntimeException {
    BadCallException(String s) {
        super(s);
    }
}
