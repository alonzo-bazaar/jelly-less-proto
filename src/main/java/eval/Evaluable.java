package eval;

import lang.LispExpression;

public interface Evaluable {
    public LispExpression eval(Environment e);
}
