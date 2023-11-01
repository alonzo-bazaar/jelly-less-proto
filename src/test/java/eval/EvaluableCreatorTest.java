package eval;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import lang.LispExpression;
import lang.LispValue;
import parse.ExpressionIterator;

public class EvaluableCreatorTest {
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
    public void testIf() {
        Evaluable ev = fromString("(if nil 10 20)");
        LispExpression le = ev.eval(env);
        if (le instanceof LispValue<?> lv && lv.get() instanceof Integer i) {
            assertEquals((int) i, 20);
        }
        else throw new AssertionError("expected a lisp integer, got "
                                      + le.toString()
                                      + " of type "
                                      + le.getClass().getCanonicalName());
    }

    @Test
    public void testSequenceReturn() {
        Evaluable ev = fromString("(begin 10 20 30)");
        LispExpression le = ev.eval(env);
        if (le instanceof LispValue<?> lv && lv.get() instanceof Integer i) {
            assertEquals((int) i, 30);
        }
        else throw new AssertionError("expected a lisp integer, got "
                                      + le.toString()
                                      + " of type "
                                      + le.getClass().getCanonicalName());
    }

    @Test
    public void testSequenceSideEffect() {
        Evaluable ev = fromString("(let ((a 1)) (begin (set a 2) a))");
        LispExpression le = ev.eval(env);
        if (le instanceof LispValue<?> lv && lv.get() instanceof Integer i) {
            assertEquals((int) i, 2);
        }
        else throw new AssertionError("expected a lisp integer, got "
                                      + le.toString()
                                      + " of type "
                                      + le.getClass().getCanonicalName());
    }

    @Test
    public void testDefineVariable() {
        fromString("(define x 10)").eval(env);
        LispExpression le = fromString("x").eval(env);
        if (le instanceof LispValue<?> lv && lv.get() instanceof Integer i) {
            assertEquals((int) i, 10);
        }
        else throw new AssertionError("expected a lisp integer, got "
                + le.toString()
                + " of type "
                + le.getClass().getCanonicalName());
    }

    @Test
    public void testDefineFunction() {
        fromString("(define str (lambda (x) (if x \"yes\" \"no\")))").eval(env);
        LispExpression leYes = fromString("(str 10)").eval(env);
        LispExpression leNo = fromString("(str nil)").eval(env);
        if (leYes instanceof LispValue<?> lvYes && lvYes.get() instanceof String sYes) {
            assertEquals(sYes, "yes");
        }
        else throw new AssertionError("expected a lisp integer, got "
                + leYes.toString()
                + " of type "
                + leYes.getClass().getCanonicalName());

        if (leNo instanceof LispValue<?> lvNo && lvNo.get() instanceof String sNo) {
            assertEquals(sNo, "no");
        }
        else throw new AssertionError("expected a lisp integer, got "
                + leNo.toString()
                + " of type "
                + leNo.getClass().getCanonicalName());
    }

    @Test
    public void testFuncallImmediate() {
        LispExpression le = fromString("((lambda (x y) (+ x (* 2 y))) 1 2)").eval(env);
        if (le instanceof LispValue<?> lv && lv.get() instanceof Integer i) {
            assertEquals((int) i, 5);
        }
        else throw new AssertionError("expected a lisp integer, got "
                + le.toString()
                + " of type "
                + le.getClass().getCanonicalName());
    }

    @Test
    public void testWhile() {
        Evaluable ev = fromString
            ("(let ((a 0) (b 0))" +
             "(while (< a 10) (set a (+ a 1)) (set b (+ 2 b)))" +
             "b)");
        LispExpression le = ev.eval(env);
        if (le instanceof LispValue<?> lv && lv.get() instanceof Integer i) {
            assertEquals((int) i, 20);
        }
        else throw new AssertionError("expected a lisp integer, got "
                                      + le.toString()
                                      + " of type "
                                      + le.getClass().getCanonicalName());
    }
}
