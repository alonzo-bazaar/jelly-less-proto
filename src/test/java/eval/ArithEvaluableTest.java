package eval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import parse.ExpressionIterator;
import parse.ParsingException;


public class ArithEvaluableTest {
    private Environment env = new Environment();

    private Evaluable fromString(String s) throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString(s);
        Object le = ei.next();
        return EvaluableCreator.fromExpression(le);
    }

    @BeforeEach
    public void refreshEnv() {
        env = Runtime.buildInitialEnvironment();
    }

    @AfterEach
    public void resetEnvironment() {
        env.reset();
    }


    @Test
    public void testAdd() throws ParsingException {
        assertEquals((int)fromString("(+ 1 2)").eval(env), 3);
    }

    @Test
    public void testSub() throws ParsingException {
        assertEquals((int)fromString("(- 1 2)").eval(env), -1);
    }

    @Test
    public void testMul() throws ParsingException {
        assertEquals((int)fromString("(* 1 2)").eval(env), 2);
    }

    @Test
    public void testDiv() throws ParsingException {
        assertEquals((double)fromString("(/ 1 2)").eval(env), 0.5, 0.00001);
    }

    @Test
    public void testGreater() throws ParsingException {
        assertTrue((boolean)fromString("(> 2 1)").eval(env));
        assertFalse((boolean)fromString("(> 1 1)").eval(env));
        assertFalse((boolean)fromString("(> 1 2)").eval(env));

        assertTrue((boolean)fromString("(> 2.0 1)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 1)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 2)").eval(env));

        assertTrue((boolean)fromString("(> 2 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1 2.0)").eval(env));

        assertTrue((boolean)fromString("(> 2.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 2.0)").eval(env));
    }

    @Test
    public void testLess() throws ParsingException {
        assertFalse((boolean)fromString("(< 2 1)").eval(env));
        assertFalse((boolean)fromString("(< 1 1)").eval(env));
        assertTrue((boolean)fromString("(< 1 2)").eval(env));

        assertFalse((boolean)fromString("(< 2 1.0)").eval(env));
        assertFalse((boolean)fromString("(< 1 1.0)").eval(env));
        assertTrue((boolean)fromString("(< 1 2.0)").eval(env));

        assertFalse((boolean)fromString("(< 2.0 1)").eval(env));
        assertFalse((boolean)fromString("(< 1.0 1)").eval(env));
        assertTrue((boolean)fromString("(< 1.0 2)").eval(env));

        assertFalse((boolean)fromString("(< 2.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(< 1.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(< 1.0 2.0)").eval(env));
    }

    @Test
    public void testGreaterEq() throws ParsingException {
        assertTrue((boolean)fromString("(>= 2 1)").eval(env));
        assertTrue((boolean)fromString("(>= 1 1)").eval(env));
        assertFalse((boolean)fromString("(>= 1 2)").eval(env));

        assertTrue((boolean)fromString("(>= 2 1.0)").eval(env));
        assertTrue((boolean)fromString("(>= 1 1.0)").eval(env));
        assertFalse((boolean)fromString("(>= 1 2.0)").eval(env));

        assertTrue((boolean)fromString("(>= 2.0 1)").eval(env));
        assertTrue((boolean)fromString("(>= 1.0 1)").eval(env));
        assertFalse((boolean)fromString("(>= 1.0 2)").eval(env));

        assertTrue((boolean)fromString("(>= 2.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(>= 1.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(>= 1.0 2.0)").eval(env));
    }

    @Test
    public void testLessEq() throws ParsingException {
        assertFalse((boolean)fromString("(<= 2 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1 2)").eval(env));

        assertFalse((boolean)fromString("(<= 2 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1 2.0)").eval(env));

        assertFalse((boolean)fromString("(<= 2.0 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 2)").eval(env));

        assertFalse((boolean)fromString("(<= 2.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 2.0)").eval(env));
    }
}
