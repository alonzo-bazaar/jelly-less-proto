package org.jelly.eval.environment;

import org.jelly.lang.data.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvFrame {
    private final Map<Symbol, Object> nameToExpr;

    public EnvFrame() {
        this.nameToExpr = new HashMap<>();
    }

    public EnvFrame(Map<Symbol, Object> nameToExpr) {
        this.nameToExpr = nameToExpr;
    }

    public EnvFrame(List<Symbol> names, List<Object> exprs) {
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
    public Object lookup(Symbol sym) {
        return nameToExpr.get(sym);
    }

    public boolean hasSymbol(Symbol sym) {
        return nameToExpr.containsKey(sym);
    }

    public void bind(Symbol sym, Object val) {
        nameToExpr.put(sym, val);
    }

    void dump() {
        // prints the entire state of the frame
        for (Symbol s : nameToExpr.keySet()) {
            System.out.println(s + " : " + nameToExpr.get(s));
        }
    }
}
