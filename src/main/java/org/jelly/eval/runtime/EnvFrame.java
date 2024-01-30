package org.jelly.eval.runtime;

import org.jelly.lang.data.LispSymbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvFrame {
    private final Map<LispSymbol, Object> nameToExpr;

    public EnvFrame() {
        this.nameToExpr = new HashMap<>();
    }

    public EnvFrame(Map<LispSymbol, Object> nameToExpr) {
        this.nameToExpr = nameToExpr;
    }

    public EnvFrame(List<LispSymbol> names, List<Object> exprs) {
        this.nameToExpr = new HashMap<>();
        /* expects names and exprs to have the same size
         * I would assert it, but I don't know if throwing an exception in the
         * constructor is a good idea
         */
        for (int i = 0; i < names.size(); ++i) {
            this.nameToExpr.put(names.get(i), exprs.get(i));
        }
    }

    // Environment does all the checking, input data assumed to be valid
    Object lookup(LispSymbol sym) {
        return nameToExpr.get(sym);
    }

    boolean hasSymbol(LispSymbol sym) {
        return nameToExpr.containsKey(sym);
    }

    void set(LispSymbol sym, Object val) {
        nameToExpr.put(sym, val);
    }

    void dump() {
        // prints the entire state of the frame
        for (LispSymbol s : nameToExpr.keySet()) {
            System.out.println(s + " : " + nameToExpr.get(s));
        }
    }
}
