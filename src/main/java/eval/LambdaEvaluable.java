package eval;

import java.util.List;
import lang.LispExpression;
import lang.LispSymbol;

public class LambdaEvaluable implements Evaluable {
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

    public LambdaEvaluable(List<LispSymbol> formalParameters,
                           SequenceEvaluable body) {
        this.formalParameters = formalParameters;
        this.body = body;
    }

    @Override
    public LispExpression eval(Environment e) {
        return new Procedure(e, formalParameters, body);
    }
}
