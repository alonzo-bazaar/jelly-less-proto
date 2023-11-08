package eval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import parse.ExpressionIterator;
import parse.ParsingException;

public class EvaluableCreatorTest {
    private Evaluable fromString(String s) throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString(s);
        Object le = ei.next();
        return EvaluableCreator.fromExpression(le);
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
    public void testIf() throws ParsingException {
        Evaluable ev = fromString("(if nil 10 20)");
        Object o = ev.eval(env);
        assertEquals((int) o, 20);
    }

    @Test
    public void testSequenceReturn() throws ParsingException {
        Evaluable ev = fromString("(begin 10 20 30)");
        Object o = ev.eval(env);
        assertEquals((int) o, 30);
    }

    @Test
    public void testSequenceSideEffect() throws ParsingException {
        Evaluable ev = fromString("(let ((a 1)) (begin (set a 2) a))");
        Object o = ev.eval(env);
        assertEquals((int) o, 2);
    }

    @Test
    public void testDefineVariable() throws ParsingException {
        fromString("(define x 10)").eval(env);
        Object o = fromString("x").eval(env);
        assertEquals((int) o, 10);
    }

    @Test
    public void testDefineFunction() throws ParsingException {
        fromString("(define str (lambda (x) (if x \"yes\" \"no\")))").eval(env);
        Object yes = fromString("(str 10)").eval(env);
        Object no = fromString("(str nil)").eval(env);
        assertEquals(yes, "yes");
        assertEquals(no, "no");
    }

    @Test
    public void testFuncallImmediate() throws ParsingException {
        Object o = fromString("((lambda (x y) (+ x (* 2 y))) 1 2)").eval(env);
        assertEquals((int) o, 5);
    }

    @Test
    public void testWhile() throws ParsingException {
        Evaluable ev = fromString
            ("(let ((a 0) (b 0))" +
             "(while (< a 10) (set a (+ a 1)) (set b (+ 2 b)))" +
             "b)");
        Object o = ev.eval(env);
        assertEquals((int) o, 20);
    }
}
