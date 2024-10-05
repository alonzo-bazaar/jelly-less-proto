package org.jelly.eval.library;

public class NoSuchLibraryException extends RuntimeException {
    public NoSuchLibraryException(String s) {
        super(s);
    }

    public NoSuchLibraryException(String s, Throwable t) {
        super(s, t);
    }
}
