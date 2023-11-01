package eval;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import parse.ExpressionIterator;

public class EvaluableCreatorTest {
    private Evaluable fromString(String s) {
        ExpressionIterator ei = new ExpressionIterator(s);
        Object le = ei.next();
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
    public void testIf() {
        Evaluable ev = fromString("(if nil 10 20)");
        Object o = ev.eval(env);
        assertEquals((int) o, 20);
    }

    @Test
    public void testSequenceReturn() {
        Evaluable ev = fromString("(begin 10 20 30)");
        Object o = ev.eval(env);
        assertEquals((int) o, 30);
    }

    @Test
    public void testSequenceSideEffect() {
        Evaluable ev = fromString("(let ((a 1)) (begin (set a 2) a))");
        Object o = ev.eval(env);
        assertEquals((int) o, 2);
    }

    @Test
    public void testDefineVariable() {
        fromString("(define x 10)").eval(env);
        Object o = fromString("x").eval(env);
        assertEquals((int) o, 10);
    }

    @Test
    public void testDefineFunction() {
        fromString("(define str (lambda (x) (if x \"yes\" \"no\")))").eval(env);
        Object yes = fromString("(str 10)").eval(env);
        Object no = fromString("(str nil)").eval(env);
        assertEquals(yes, "yes");
        assertEquals(no, "no");
    }

    @Test
    public void testFuncallImmediate() {
        Object o = fromString("((lambda (x y) (+ x (* 2 y))) 1 2)").eval(env);
        assertEquals((int) o, 5);
    }

    @Test
    public void testWhile() {
        Evaluable ev = fromString
            ("(let ((a 0) (b 0))" +
             "(while (< a 10) (set a (+ a 1)) (set b (+ 2 b)))" +
             "b)");
        Object o = ev.eval(env);
        assertEquals((int) o, 20);
    }
}
