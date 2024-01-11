package org.jelly.parse.token;

public final class NormalToken extends Token {
    public NormalToken(String s) {
        super(s);
    }

    @Override
    public String toString() {
        return "NormalToken(\"" + getString() + "\")";
    }
}
