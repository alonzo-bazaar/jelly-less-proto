package org.jelly.eval.evaluable;

import java.security.InvalidParameterException;
import java.util.List;

import org.jelly.eval.procedure.Procedure;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.builtinfuns.Utils;

public class FuncallEvaluable implements Evaluable {
    private final Evaluable proc;
    private final List<Object> args;

    public FuncallEvaluable(Evaluable proc, List<Object> args) {
        this.proc = proc;
        this.args = args;
    }

    @Override
    public Object eval(Environment env) {
        Object funObj = proc.eval(env);
        if (funObj instanceof Procedure fun) {
            return fun.call(Utils.evlist(args, env));
        }
        throw new InvalidParameterException(proc + " evaluates to " + funObj + ", which is not a procedure, and thus cannot be called");
    }
}
    
