package eval;

import java.util.List;

import lang.LispSymbol;

public class LetEvaluation implements Evaluable {
    private List<LispSymbol> names;
    private List<Evaluable> uncomputed_vals;
    private SequenceEvaluable body;

    public LetEvaluation(List<LispSymbol> names,
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
        Object res = body.eval(e.extend(new EnvFrame(names, computed_vals)));
        return res;
    }
}
