package org.jelly.lang;

public interface LispList {
    public Object getCar();
    public Object getCdr();

    public int length();

    public Object nth(int n);
    public Object nthCdr(int n);

    public Object last();
}
