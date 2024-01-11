package org.jelly.parse.token;

abstract public class Token {
    private String s;
    public Token(String s) {
        this.s = s;
    }

    public String getString() {
        return this.s;
    }
}

