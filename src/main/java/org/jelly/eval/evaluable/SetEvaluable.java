package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.runtime.error.JellyError;
import org.jelly.lang.data.Symbol;
import org.jelly.lang.data.Constants;

public class SetEvaluable implements Evaluable {
    private final Symbol sym;
    private final Evaluable uncomputedVal;

    public SetEvaluable(Symbol sym, Evaluable val) {
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
            throw new JellyError("error while setting " + sym, t);
        }
    }
}
