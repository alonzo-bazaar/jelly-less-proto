package eval;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.Constants;
import lang.LispExpression;
import lang.LispValue;
import lang.LispSymbol;
import parse.ExpressionIterator;

public class BuiltinFuncallEvaluableTest {
    private Evaluable fromString(String s) {
        ExpressionIterator ei = new ExpressionIterator(s);
        LispExpression le = ei.next();
        return EvaluableCreator.fromExpression(le);
    }

    private Environment env = new Environment();

    @Before
    public void refreshEnv() {
        env = new Environment();
    }

    @After
    public void resetEnvironment() {
        env.reset();
    }

    @Test
    public void testLessThan() {
        Evaluable less = fromString("(> 10 10.1)");
    }
}
