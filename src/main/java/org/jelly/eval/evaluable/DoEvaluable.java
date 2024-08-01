package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.eval.environment.errors.VariableDoesNotExistException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Symbol;

public class DoEvaluable implements Evaluable {
    private List<Symbol> vars;
    private List<Evaluable> initForms;
    private List<Evaluable> updateForms;
    private SequenceEvaluable body;
    private Evaluable stopCondition;
    private Evaluable returnOnStop;

    public DoEvaluable(List<Symbol> vars,
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

        while (ListUtils.isFalse(stopCondition.eval(exEnv))) {
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
