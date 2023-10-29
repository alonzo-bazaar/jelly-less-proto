package eval;

import lang.Ops;
import lang.Constants;
import lang.LispExpression;

public class WhileLoopEvaluable implements Evaluable {
    private Evaluable cond;
    private SequenceEvaluable body;

    private LispExpression runningValue;

    public WhileLoopEvaluable(Evaluable cond, SequenceEvaluable body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public LispExpression eval(Environment e) {
        runningValue = Constants.NIL;
        LispExpression check = cond.eval(e);
        while(!Ops.isNil(check) &&
              !Ops.isFalse(check)) {
            runningValue = body.eval(e);
            check = cond.eval(e);
        }
        return runningValue;
    }
}
