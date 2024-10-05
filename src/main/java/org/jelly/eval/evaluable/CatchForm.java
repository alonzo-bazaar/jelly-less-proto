package org.jelly.eval.evaluable;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.Symbol;
import org.jelly.lang.javaffi.WrappedCallException;

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
        if (t instanceof WrappedCallException)
            return wrappedCallIsCompatible(t);

        return classCompatible(t.getClass());
    }

    public boolean wrappedCallIsCompatible(Throwable t) {
        /*
         * logica a parte per wrapped call visto che l'eccezione che si vuole
         * catchare è spesso racchiusa sotto qualche strato di .getCause()
         * fa abbastanza schifo
         */ 
        int maxDepth = 3; // massima profondità che siamo disposti a guardare
        for(Throwable th = t;
            th != null && maxDepth > 0;
            th = th.getCause(), maxDepth--) {
            if(classCompatible(th.getClass()))
                return true;
        }
        return false;
    }

    private boolean classCompatible(Class<?> clazz) {
        if (clazz.getSimpleName().equals(throwableClassName))
            return true;

        for(Class<?> c : clazz.getInterfaces()) {
            if (c.getSimpleName().equals(throwableClassName))
                return true;
        }
        for(Class<?> c : clazz.getClasses()) {
            if (c.getSimpleName().equals(throwableClassName))
                return true;
        }

        if(clazz.getSuperclass() == null)
            return false;

        return classCompatible(clazz.getSuperclass());
    }

    public Object evalCatch(Throwable caught, Environment env) {
        Map<Symbol, Object> binds = new HashMap<>();
        binds.put(exceptionName, caught);
        Environment ex = env.extend(new EnvFrame(binds));
        return body.eval(ex);
    }
}
