package eval;

import lang.Constants;

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
    public Object eval(Environment e) {
        if (Utils.isTrue(condition.eval(e))) {
            return ifPart.eval(e);
        }
        return elsePart.eval(e);
    }
}
