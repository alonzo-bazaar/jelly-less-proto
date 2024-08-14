package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.evaluable.errors.CatchFailedException;

import java.util.List;

public class TryCatchEvaluable implements Evaluable {
    private final SequenceEvaluable body;
    // private final List<Pair<Class<?>, SequenceEvaluable>> catches;
    private final List<CatchForm> catches;

    public TryCatchEvaluable(SequenceEvaluable body, List<CatchForm> catches) {
        this.body = body;
        this.catches = catches;
    }

    @Override
    public Object eval(Environment env) {
        try {
            return body.eval(env);
        } catch (Throwable t) {
            for(CatchForm c : catches) {
                // if(c.first.isAssignableFrom(t.getClass())) {
                if(c.isCompatible(t)) { // very ugly
                    return c.evalCatch(t, env);
                }
            }
            throw new CatchFailedException("no catch clause in try/catch caught the thrown exception", t);
        }
    }
}
