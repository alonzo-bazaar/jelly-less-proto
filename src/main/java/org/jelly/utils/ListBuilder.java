package org.jelly.utils;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.ConsList;

public class ListBuilder {
    ConsList initial;
    Cons lastCons;

    public ListBuilder() {
        this.initial = Constants.NIL;
    }

    public ListBuilder addLast(Object o) {
        if (initial == Constants.NIL) {
            singleVal(o);
        }
        else {
            lastCons.setCdr(new Cons(o, Constants.NIL));
            lastCons = (Cons)lastCons.getCdr();
        }
        return this;
    }

    public ListBuilder addFirst(Object o) {
        if (initial == Constants.NIL) {
            singleVal(o);
        }
        else {
            initial = new Cons(o, initial);
        }
        return this;
    }

    void singleVal(Object o) {
        initial = new Cons(o, Constants.NIL);
        lastCons = (Cons)initial;
    }

    public ConsList get() {
        return initial;
    }
}
