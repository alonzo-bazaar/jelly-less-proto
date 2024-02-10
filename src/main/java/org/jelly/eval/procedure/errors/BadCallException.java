package org.jelly.eval.procedure.errors;

public class BadCallException extends RuntimeException {
    BadCallException(String s) {
        super(s);
    }
}
