package org.jelly.eval.evaluable;

import org.jelly.eval.runtime.Environment;
import org.jelly.lang.LispSymbol;

public class LookupEvaluable implements Evaluable {
    private LispSymbol sym;

    public LookupEvaluable(LispSymbol sym) {
        this.sym = sym;
    }

    @Override
    public Object eval(Environment e) {
        return e.lookup(sym);
    }

    @Override
    public String toString() {
        return "Lookup of symbol (" + sym + ")";
    }
}
