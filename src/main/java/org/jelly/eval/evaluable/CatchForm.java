package org.jelly.eval.evaluable;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.Symbol;

import java.util.HashMap;
import java.util.Map;

public class CatchForm {
    private final Symbol exceptionName;
    private final SequenceEvaluable body;
    private final String throwableClassName;

    public CatchForm(Symbol throwableClassName, Symbol exceptionName, SequenceEvaluable body) {
        this.throwableClassName = throwableClassName.name();
        this.exceptionName = exceptionName;
        this.body = body;
    }

    public boolean isCompatible(Throwable t) {
        return t.getClass().getSimpleName().equals(throwableClassName);
    }

    public Object evalCatch(Throwable caught, Environment env) {
        Map<Symbol, Object> binds = new HashMap<>();
        binds.put(exceptionName, caught);
        Environment ex = env.extend(new EnvFrame(binds));
        return body.eval(ex);
    }
}
