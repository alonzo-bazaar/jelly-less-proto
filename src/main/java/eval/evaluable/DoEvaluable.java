package eval.evaluable;

import java.util.List;

import eval.runtime.EnvFrame;
import eval.runtime.Environment;
import eval.runtime.errors.VariableDoesNotExistException;
import eval.utils.Utils;
import lang.LispSymbol;

public class DoEvaluable implements Evaluable {
    private List<LispSymbol> vars;
    private List<Evaluable> initForms;
    private List<Evaluable> updateForms;
    private SequenceEvaluable body;
    private Evaluable stopCondition;
    private Evaluable returnOnStop;

    public DoEvaluable(List<LispSymbol> vars,
                       List<Evaluable> initForms,
                       List<Evaluable> updateForms,
                       SequenceEvaluable body,
                       Evaluable stopCondition,
                       Evaluable returnOnStop) {
        this.vars = vars;
        this.initForms = initForms;
        this.updateForms = updateForms;
        this.stopCondition = stopCondition;
        this.returnOnStop = returnOnStop;
        this.body = body;
    }

    @Override
    public Object eval(Environment env) {
        EnvFrame frame = new EnvFrame(vars, this.initForms
                                      .stream()
                                      .map(a -> a.eval(env))
                                      .toList());
        Environment exEnv = env.extend(frame);

        while (Utils.isFalse(stopCondition.eval(exEnv))) {
            body.eval(exEnv);
            updateFrame(frame, exEnv);
        }

        return returnOnStop.eval(exEnv);
    }

    private void updateFrame(EnvFrame frame, Environment env) {
        // il do normale aggiorna le variabili "in parallelo"
        // quindi senza che il valore aggiornato di una variabile
        // vada a influenzare il valore aggiornato di un'altra variable
        // quindi prima si aggiornano tutti, poi si assegnano di botta

        try {
            List<Object> updated = updateForms.stream().map(a -> a.eval(env)).toList();
            for (int i = 0; i < vars.size(); ++i) {
                env.set(vars.get(i), updated.get(i));
            }
        } catch(VariableDoesNotExistException ignored) {} // dovrebbe essere unreachable
    }
}
