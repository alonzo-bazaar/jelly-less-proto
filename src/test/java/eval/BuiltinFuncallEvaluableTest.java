package eval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import parse.ExpressionIterator;

public class BuiltinFuncallEvaluableTest {
    private Evaluable fromString(String s) {
        ExpressionIterator ei = new ExpressionIterator(s);
        Object o = ei.next();
        return EvaluableCreator.fromExpression(o);
    }

    private Environment env = new Environment();

    @BeforeEach
    public void refreshEnv() {
        env = new Environment();
    }

    @AfterEach
    public void resetEnvironment() {
        env.reset();
    }

    @Test
    public void testLessThan() {
        Evaluable less = fromString("(> 10 10.1)");
        assertFalse((boolean)less.eval(env));
    }

    @Test
    public void testMoreThan() {
        Evaluable more = fromString("(< 10 10.1)");
        assertTrue((boolean)more.eval(env));
    }
}
