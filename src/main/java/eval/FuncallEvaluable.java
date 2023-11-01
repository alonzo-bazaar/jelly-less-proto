package eval;

import java.security.InvalidParameterException;
import java.util.List;
import lang.LispExpression;

public class FuncallEvaluable implements Evaluable {
    private Evaluable proc;
    private List<LispExpression> args;

    public FuncallEvaluable(Evaluable proc, List<LispExpression> args) {
        this.proc = proc;
        this.args = args;
    }

    @Override
    public LispExpression eval(Environment env) {
        if (proc.eval(env) instanceof Procedure fun) {
            return fun.call(args);
        }
        throw new InvalidParameterException(proc + "does not evaluate to a procedure, cannot call it");
    }
}
    
