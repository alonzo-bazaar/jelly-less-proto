package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.Symbol;

public class LookupEvaluable implements Evaluable {
    private Symbol sym;

    public LookupEvaluable(Symbol sym) {
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
