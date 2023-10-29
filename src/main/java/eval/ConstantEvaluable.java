package eval;

import lang.LispExpression;

public class ConstantEvaluable implements Evaluable {
    private LispExpression exp;
    public ConstantEvaluable(LispExpression exp) {
        this.exp = exp;
    }

    @Override
    public LispExpression eval(Environment e) {
        return exp;
    }
}
