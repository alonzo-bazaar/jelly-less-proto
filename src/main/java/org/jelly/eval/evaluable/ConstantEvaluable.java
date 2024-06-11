package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;

public class ConstantEvaluable implements Evaluable {
    private final Object exp;
    public ConstantEvaluable(Object exp) {
        this.exp = exp;
    }

    @Override
    public Object eval(Environment e) {
        return exp;
    }
}
