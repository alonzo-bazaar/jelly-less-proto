package eval;

import java.security.InvalidParameterException;
import java.util.List;

public class FuncallEvaluable implements Evaluable {
    private Evaluable proc;
    private List<Object> args;

    public FuncallEvaluable(Evaluable proc, List<Object> args) {
        this.proc = proc;
        this.args = args;
    }

    @Override
    public Object eval(Environment env) {
        if (proc.eval(env) instanceof Procedure fun) {
            return fun.call(args);
        }
        throw new InvalidParameterException(proc + "does not evaluate to a procedure, cannot call it");
    }
}
    
