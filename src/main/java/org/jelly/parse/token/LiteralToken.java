package org.jelly.parse.token;

public final class LiteralToken<T> extends Token {
    T obj;
    LiteralToken(String s, T obj) {
        super(s);
        this.obj = obj;
    }
    public T getVal() {
        return this.obj;
    }

    @Override
    public String toString() {
        return "LiteralToken<" + obj.getClass().getCanonicalName() +">(" + obj + ")";
    }
}
