package org.jelly.lang.data;

public class Nil implements ConsList {
    public Nil() {
        // niente
    }
    @Override
    public Object getCar() {
        return Constants.NIL;
    }
    @Override
    public Object getCdr() {
        return Constants.NIL; 
    }
    @Override
    public int length() {
        return 0;
    }
    @Override
    public Object nth(int n) {
        return Constants.NIL; 
    }
    @Override
    public Object nthCdr(int n) {
        return Constants.NIL; 
    }
    @Override
    public Object last() {
        return Constants.NIL;
    }
    @Override
    public String toString() {
        return "nil";
    }
}
