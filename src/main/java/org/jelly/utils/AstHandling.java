package org.jelly.utils;

import org.jelly.lang.data.Symbol;

public class AstHandling {
    public static Symbol requireSymbol(Object o) {
        if(o instanceof Symbol s) {
            return s;
        }
        throw new ClassCastException("object " + o + " of type " + o.getClass().getCanonicalName() + " was supposed to be a symbol");
    }
}
