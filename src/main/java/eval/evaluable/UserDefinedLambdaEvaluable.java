package eval.evaluable;

import java.util.List;

import eval.procedure.UserDefinedProcedure;
import eval.runtime.Environment;
import lang.LispSymbol;

public class UserDefinedLambdaEvaluable implements LambdaEvaluable {
    /* evaluating a lambda expression
     * (aka "running" a lambda evaluable)
     * returns a procedure
     *
     * lambda and let are somewhat similar constructs
     * this code looks a lot like that for LetEvaluable,
     * save for the fact computing the values is left to the returned procedure
     */

    private final List<LispSymbol> formalParameters;
    private final SequenceEvaluable body;

    public UserDefinedLambdaEvaluable(List<LispSymbol> formalParameters,
                                      SequenceEvaluable body) {
        this.formalParameters = formalParameters;
        this.body = body;
    }

    @Override
    public UserDefinedProcedure eval(Environment e) {
        return new UserDefinedProcedure(e, formalParameters, body);
    }
}
