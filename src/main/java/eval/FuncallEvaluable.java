package eval;

import java.util.List;
import lang.LispExpression;

public class FuncallEvaluable implements Evaluable {
    private Procedure proc;
    private List<LispExpression> args;

    public FuncallEvaluable(Procedure proc, List<LispExpression> args) {
        this.proc = proc;
        this.args = args;
    }

    @Override
    public LispExpression eval(Environment env) {
        return proc.call(args);
    }
}
    
