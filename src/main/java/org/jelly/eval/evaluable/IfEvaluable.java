package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.utils.ListUtils;


public class IfEvaluable implements Evaluable {
    private final Evaluable condition;
    private final Evaluable ifPart;
    private final Evaluable elsePart;

    public IfEvaluable(Evaluable condition,
                       Evaluable ifPart,
                       Evaluable elsePart) {
        this.condition = condition;
        this.ifPart = ifPart;
        this.elsePart = elsePart;
    }

    @Override
    public Object eval(Environment e) {
        if (ListUtils.isTrue(condition.eval(e))) {
            return ifPart.eval(e);
        }
        return elsePart.eval(e);
    }
}
