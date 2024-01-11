package org.jelly.lang.javaffi;

public class NotAnErrorException extends RuntimeException {
    public NotAnErrorException(String s) {
        super(s);
    }
}
