package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.Constants;

public class SetEvaluable implements Evaluable {
    private final LispSymbol sym;
    private final Evaluable uncomputedVal;

    public SetEvaluable(LispSymbol sym, Evaluable val) {
        this.sym = sym;
        this.uncomputedVal = val;
    }

    @Override
    public Object eval(Environment e) {
        try {
            Object computed_val = uncomputedVal.eval(e);
            e.set(sym, computed_val);
            return computed_val;
        }
        catch(Throwable t) {
            return Constants.NIL;
        }
    }
}
