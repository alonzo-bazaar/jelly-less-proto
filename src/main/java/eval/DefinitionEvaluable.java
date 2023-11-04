package eval;

import lang.LispSymbol;

import static lang.Constants.NIL;

public class DefinitionEvaluable implements Evaluable {
    private final LispSymbol sym;
    private final Evaluable uncomputedVal;

    public DefinitionEvaluable(LispSymbol sym, Evaluable val) {
        this.sym = sym;
        this.uncomputedVal = val;
    }

    @Override
    public Object eval(Environment e) {
        try {
            Object computed_val = uncomputedVal.eval(e);
            e.define(sym, computed_val);
            return computed_val;
        }
        catch(Throwable t) {
            System.out.println("error while defining " + sym.getName());
            System.out.println(t.getClass().getCanonicalName());
            System.out.println(t.getMessage());
            return NIL;
        }
    }
}
