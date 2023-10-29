package eval;

import lang.LispExpression;
import lang.LispSymbol;
import lang.Constants;

public class SetEvaluable implements Evaluable {
    private LispSymbol sym;
    private Evaluable uncomputed_val;

    public SetEvaluable(LispSymbol sym, Evaluable val) {
        this.sym = sym;
        this.uncomputed_val = val;
    }

    @Override
    public LispExpression eval(Environment e) {
        try {
            LispExpression computed_val = uncomputed_val.eval(e);
            e.set(sym, computed_val);
            return computed_val;
        }
        catch(Throwable t) {
            return Constants.NIL;
        }
    }
}
