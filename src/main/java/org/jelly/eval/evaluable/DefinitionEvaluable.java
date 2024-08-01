package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.Symbol;

import static org.jelly.lang.data.Constants.NIL;

public class DefinitionEvaluable implements Evaluable {
    private final Symbol sym;
    private final Evaluable uncomputedVal;

    public DefinitionEvaluable(Symbol sym, Evaluable val) {
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
            System.out.println("error while defining " + sym.name());
            System.out.println(t.getClass().getCanonicalName());
            System.out.println(t.getMessage());
            return NIL;
        }
    }

}
