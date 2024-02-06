package org.jelly.parse.errors;

import org.jelly.lang.errors.CompilationError;

public class ParsingException extends CompilationError {
    public ParsingException(String s) {
        super(s);
    }
    public ParsingException(String s, Throwable t) {
        super(s, t);
    }
}
