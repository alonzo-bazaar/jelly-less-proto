package org.jelly.eval.evaluable;

import org.jelly.eval.runtime.Environment;
import org.jelly.eval.utils.Utils;
import org.jelly.lang.data.Constants;

public class WhileLoopEvaluable implements Evaluable {
    private Evaluable cond;
    private SequenceEvaluable body;


    public WhileLoopEvaluable(Evaluable cond, SequenceEvaluable body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public Object eval(Environment e) {
        Object runningValue = Constants.NIL;
        Object check = cond.eval(e);
        while(Utils.isTrue(check)) {
            runningValue = body.eval(e);
            check = cond.eval(e);
        }
        return runningValue;
    }
}
