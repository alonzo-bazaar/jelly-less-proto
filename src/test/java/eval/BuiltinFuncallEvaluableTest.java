package eval;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import parse.ExpressionIterator;

public class BuiltinFuncallEvaluableTest {
    private Evaluable fromString(String s) {
        ExpressionIterator ei = new ExpressionIterator(s);
        Object o = ei.next();
        return EvaluableCreator.fromExpression(o);
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
