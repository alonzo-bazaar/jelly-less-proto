package eval.evaluable;

import eval.runtime.Environment;

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
