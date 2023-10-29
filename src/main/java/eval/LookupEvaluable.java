package eval;

import lang.LispExpression;
import lang.LispSymbol;

public class LookupEvaluable implements Evaluable {
    private LispSymbol sym;

    public LookupEvaluable(LispSymbol sym) {
        this.sym = sym;
    }

    @Override
    public LispExpression eval(Environment e) {
        return e.lookup(sym);
    }
}
