package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.runtime.EnvFrame;
import org.jelly.eval.runtime.Environment;
import org.jelly.lang.data.LispSymbol;

public class LetEvaluable implements Evaluable {
    private List<LispSymbol> names;
    private List<Evaluable> uncomputed_vals;
    private SequenceEvaluable body;

    public LetEvaluable(List<LispSymbol> names,
                        List<Evaluable> evals,
                        SequenceEvaluable body) {
        this.names = names;
        this.uncomputed_vals = evals;
        this.body = body;
    }

    @Override
    public Object eval(Environment e) {
        List<Object> computed_vals =
            uncomputed_vals
            .stream()
            .map(exp -> exp.eval(e))
            .toList();
        return body.eval(e.extend(new EnvFrame(names, computed_vals)));
    }
}
