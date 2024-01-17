package org.jelly.lang;

public class ListBuilder {
    LispList initial;
    Cons lastCons;

    public ListBuilder() {
        this.initial = Constants.NIL;
    }

    public void addLast(Object o) {
        if (initial == Constants.NIL) {
            singleVal(o);
        }
        else {
            lastCons.setCdr(new Cons(o, Constants.NIL));
            lastCons = (Cons)lastCons.getCdr();
        }
    }

    public void addFirst(Object o) {
        if (initial == Constants.NIL) {
            singleVal(o);
        }
        else {
            initial = new Cons(o, initial);
        }
    }

    void singleVal(Object o) {
        initial = new Cons(o, Constants.NIL);
        lastCons = (Cons)initial;
    }

    public LispList get() {
        return initial;
    }
}
