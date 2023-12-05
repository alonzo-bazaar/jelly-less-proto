package eval.evaluable;

import eval.runtime.Environment;
import lang.LispSymbol;

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
