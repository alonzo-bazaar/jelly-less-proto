package eval.evaluable;

import eval.runtime.Environment;
import lang.LispSymbol;
import lang.Constants;

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
