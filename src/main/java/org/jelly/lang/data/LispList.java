package org.jelly.lang.data;

public interface LispList {
    public Object getCar();
    public Object getCdr();

    public int length();

    public Object nth(int n);
    public Object nthCdr(int n); // mi sa era meglio LispList

    public Object last();
}
