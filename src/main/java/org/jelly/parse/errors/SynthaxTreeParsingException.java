package org.jelly.parse.errors;

public class SynthaxTreeParsingException extends ParsingException {
    public SynthaxTreeParsingException(String s) {
        super(s);
    }
    public SynthaxTreeParsingException(String s, Throwable t) {
        super(s, t);
    }
}
