package org.jelly.lang.javaffi;

public class WrappedCallException extends RuntimeException {
    public WrappedCallException(String s, Throwable cause) {
        super(s, cause);
    }
}
