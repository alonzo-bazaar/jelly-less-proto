package org.jelly.eval.evaluable.formbuild;

public class MalformedFormException extends Exception {
    public MalformedFormException(String s) {
        super(s);
    }

    public MalformedFormException(String s, Throwable c) {
        super(s, c);
    }
}
