package eval.evaluable;

import eval.procedure.Procedure;
import eval.runtime.Environment;
import eval.utils.ArgUtils;

import java.security.InvalidParameterException;
import java.util.List;

public class FuncallEvaluable implements Evaluable {
    private final Evaluable proc;
    private final List<Object> args;

    public FuncallEvaluable(Evaluable proc, List<Object> args) {
        this.proc = proc;
        this.args = args;
    }

    @Override
    public Object eval(Environment env) {
        Object fun_ = proc.eval(env);
        if (fun_ instanceof Procedure fun) {
            return fun.call(ArgUtils.evlist(args, env));
        }
        throw new InvalidParameterException(proc + " evaluates to " + fun_ + ", which is not a procedure, and thus cannot be called");
    }
}
    
