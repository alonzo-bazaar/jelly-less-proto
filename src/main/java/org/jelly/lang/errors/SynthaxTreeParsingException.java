package org.jelly.lang.errors;

public class SynthaxTreeParsingException extends ParsingException {
    public SynthaxTreeParsingException(String s) {
        super(s);
    }

    public SynthaxTreeParsingException(String s, int r, int c) {
        super(s,r,c);
    }
}
