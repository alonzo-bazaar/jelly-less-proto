package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.evaluable.procedure.UserDefinedProcedure;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.Symbol;

public class UserDefinedLambdaEvaluable implements Evaluable, LambdaEvaluable {
    /* evaluating a lambda expression
     * (aka "running" a lambda evaluable)
     * returns a procedure
     *
     * lambda and let are somewhat similar constructs
     * this code looks a lot like that for LetEvaluable,
     * save for the fact computing the values is left to the returned procedure
     */

    private final List<Symbol> formalParameters;
    private final SequenceEvaluable body;

    public UserDefinedLambdaEvaluable(List<Symbol> formalParameters,
                                      SequenceEvaluable body) {
        this.formalParameters = formalParameters;
        this.body = body;
    }

    @Override
    public UserDefinedProcedure eval(Environment e) {
        return new UserDefinedProcedure(e, formalParameters, body);
    }
}
