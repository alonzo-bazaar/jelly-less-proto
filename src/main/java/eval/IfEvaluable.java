package eval;

import lang.LispExpression;
import lang.Ops;

public class IfEvaluable implements Evaluable {
    private Evaluable condition;
    private Evaluable ifPart;
    private Evaluable elsePart;

    public IfEvaluable(Evaluable condition,
                       Evaluable ifPart,
                       Evaluable elsePart) {
        this.condition = condition;
        this.ifPart = ifPart;
        this.elsePart = elsePart;
    }

    @Override
    public LispExpression eval(Environment e) {
        LispExpression check = condition.eval(e);
        if (!Ops.isNil(check) &&
            !Ops.isFalse(check)) {
            return ifPart.eval(e);
        }
        return elsePart.eval(e);
    }
}
