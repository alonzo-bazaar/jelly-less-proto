package eval;

import parse.ExpressionIterator;
import parse.ParsingException;
import parse.UnbalancedParensException;

public class Machine {
    // interface to the underlying "lisp machine"
    /*
     * java code will use the a Machine object to call lisp code
     * and fetch the results
     */
    private Environment env = new Environment();

    public Object evalString(String s) throws ParsingException {
        ExpressionIterator ei = new ExpressionIterator(s);
        while (ei.hasNext()) {
            Object le = ei.next();
            if (ei.hasNext())
                this.evalExpr(le);
            else
                return this.evalExpr(le);
        }
        // si spera unreachable
        return null;
    }

    public Object evalExpr(Object le) {
        Evaluable ev = EvaluableCreator.fromExpression(le);
        return ev.eval(env);
    }

    public Object evalFile(String s) {
        return null;
    }

    public Object get(String name) {
        return null;
    }

    public void set(String name, Object value) {
        return;
    }

    public void reset() {
        return;
    }

    // boh
    Object lispEvalString(String s) {
        return null;
    }

    Object lispEvalFile(String s) {
        return null;
    }

    Object extractObjectFromExpression(Object le) {
        return null;
    }
}
