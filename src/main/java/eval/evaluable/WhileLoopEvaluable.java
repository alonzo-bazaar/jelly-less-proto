package eval.evaluable;

import eval.runtime.Environment;
import eval.utils.Utils;
import lang.Constants;

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
