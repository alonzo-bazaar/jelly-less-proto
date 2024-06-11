package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.LispSymbol;

public class LetEvaluable implements Evaluable {
    private final List<LispSymbol> names;
    private final List<Evaluable> uncomputedVals;
    private final SequenceEvaluable body;

    public LetEvaluable(List<LispSymbol> names,
                        List<Evaluable> evals,
                        SequenceEvaluable body) {
        this.names = names;
        this.uncomputedVals = evals;
        this.body = body;
    }

    @Override
    public Object eval(Environment e) {
        List<Object> computedVals =
            uncomputedVals
            .stream()
            .map(exp -> exp.eval(e))
            .toList();
        return body.eval(e.extend(new EnvFrame(names, computedVals)));
    }
}
