package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Constants;

public class WhileEvaluable implements Evaluable {
    private Evaluable cond;
    private SequenceEvaluable body;

    public WhileEvaluable(Evaluable cond, SequenceEvaluable body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public Object eval(Environment e) {
        Object runningValue = Constants.NIL;
        Object check = cond.eval(e);
        while(ListUtils.isTrue(check)) {
            runningValue = body.eval(e);
            check = cond.eval(e);
        }
        return runningValue;
    }
}
