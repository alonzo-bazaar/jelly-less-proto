package eval.evaluable;

import eval.procedure.Procedure;
import eval.runtime.Environment;

public interface LambdaEvaluable extends Evaluable {
    /* evaluating a lambda expression
     * (aka "running" a lambda evaluable)
     * returns a procedure
     *
     * lambda and let are somewhat similar constructs
     * this code looks a lot like that for LetEvaluable,
     * save for the fact that here the job of computing the values is left
     * to the returned procedure
     */

    @Override
    public Procedure eval(Environment e);
}
