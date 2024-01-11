package org.jelly.eval.procedure;

public class BadCallException extends RuntimeException {
    BadCallException(String s) {
        super(s);
    }
}
