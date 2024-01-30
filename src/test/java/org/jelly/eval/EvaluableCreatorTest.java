package org.jelly.eval;

import org.jelly.eval.evaluable.AndEvaluable;
import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.OrEvaluable;
import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.LispList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.jelly.lang.errors.ParsingException;

public class EvaluableCreatorTest extends BaseEvaluableTest {
    @Test
    public void testQuoteSymbol() throws ParsingException {
        Evaluable ev = fromString("(quote a)");
        Object o = ev.eval(env);
        assertEquals("a", ((LispSymbol)o).getName());
    }

    @Test
    public void testQuoteSymbolDoesNotLookup() throws ParsingException {
        Evaluable ev = fromString("(let ((a 10)) (quote a))");
        Object o = ev.eval(env);
        assertEquals("a", ((LispSymbol)o).getName());
    }

    @Test
    public void testQuoteNumber() throws ParsingException {
        Evaluable ev = fromString("(+ (quote 3) (quote 4))");
        Object o = ev.eval(env);
        assertEquals(7, (int)o);
    }

    @Test
    public void testQuoteString() throws ParsingException {
        Evaluable ev = fromString("(quote \"waluigi\")");
        Object o = ev.eval(env);
        assertEquals("waluigi", (String)o);
    }

    @Test
    public void testQuoteList() throws ParsingException {
        Evaluable ev = fromString("(quote (1 2 3))");
        Object o = ev.eval(env);
        assertEquals(1, (int)((LispList)o).nth(0));
        assertEquals(2, (int)((LispList)o).nth(1));
        assertEquals(3, (int)((LispList)o).nth(2));
    }

    @Test
    public void testQuoteListDynamicallyTypedThing() throws ParsingException {
        Evaluable ev = fromString("(quote (1 \"test\" yee))");
        Object o = ev.eval(env);
        assertEquals(1, (int)((LispList)o).nth(0));
        assertEquals("test", (String)((LispList)o).nth(1));
        assertEquals("yee", ((LispSymbol)(((LispList)o).nth(2))).getName());
    }

    @Test
    public void testIf() throws ParsingException {
        Evaluable ev = fromString("(if #f 10 20)");
        Object o = ev.eval(env);
        assertEquals(20, (int) o);
    }

    @Test
    public void testWhile() throws ParsingException {
        Evaluable ev = fromString
            ("(let ((a 0) (b 0))" +
             "(while (< a 10) (set! a (+ a 1)) (set! b (+ 2 b)))" +
             "b)");
        Object o = ev.eval(env);
        assertEquals(20, (int) o);
    }

    @Test
    public void testSequenceReturn() throws ParsingException {
        Evaluable ev = fromString("(begin 10 20 30)");
        Object o = ev.eval(env);
        assertEquals(30, (int) o);
    }

    @Test
    public void testSequenceSideEffect() throws ParsingException {
        Evaluable ev = fromString("(let ((a 1)) (begin (set! a 2) a))");
        Object o = ev.eval(env);
        assertEquals(2, (int) o);
    }

    @Test
    public void testDefineVariable() throws ParsingException {
        fromString("(define x 10)").eval(env);
        Object o = fromString("x").eval(env);
        assertEquals(10, (int) o);
    }

    @Test
    public void testDefineFunctionLambda() throws ParsingException {
        fromString("(define str (lambda (x) (if x \"yes\" \"no\")))").eval(env);
        Object yes = fromString("(str 10)").eval(env);
        Object yesT = fromString("(str #t)").eval(env);
        Object yesNil = fromString("(str nil)").eval(env);
        Object no = fromString("(str #f)").eval(env);

        assertEquals("yes", yes);
        assertEquals("yes", yesT);
        assertEquals("yes", yesNil);
        assertEquals("no", no);
    }

    @Test
    public void testDefineFunction() throws ParsingException {
        fromString("(define (str x) (if x \"yes\" \"no\"))").eval(env);
        Object yes = fromString("(str 10)").eval(env);
        Object yesT = fromString("(str #t)").eval(env);
        Object yesNil = fromString("(str nil)").eval(env);
        Object no = fromString("(str #f)").eval(env);

        assertEquals("yes", yes);
        assertEquals("yes", yesT);
        assertEquals("yes", yesNil);
        assertEquals("no", no);
    }

    @Test
    public void testArithCall() throws ParsingException {
        Object o = fromString("(+ 2 3)").eval(env);
        assertEquals(5, (int) o);
    }

    @Test
    public void testArithCallSymbols() throws ParsingException {
        Object o = fromString("(let ((a 2)) (+ a 3))").eval(env);
        assertEquals(5, (int) o);
    }

    @Test
    public void testArithCallSubexpressions() throws ParsingException {
        Object o = fromString("(let ((a 2)) (+ a (+ a 3)))").eval(env);
        assertEquals(7, (int) o);
    }

    @Test
    public void testJustLambda() throws ParsingException {
        Object o = fromString("((lambda (x) x) 3)").eval(env);
        assertEquals(3, (int) o);
    }

    @Test
    public void testLetLambda() throws ParsingException {
        Object o = fromString("(let ((a (lambda (x) x))) (a 3))").eval(env);
        assertEquals(3, (int) o);
    }

    @Test
    public void testFuncallImmediate() throws ParsingException {
        Object o = fromString("((lambda (x y) (+ x (* 2 y))) 1 2)").eval(env);
        assertEquals(5, (int) o);
    }

    @Test
    public void testLessThan() throws ParsingException {
        Evaluable less = fromString("(> 10 10.1)");
        assertFalse((boolean)less.eval(env));
    }

    @Test
    public void testMoreThan() throws ParsingException {
        Evaluable more = fromString("(< 10 10.1)");
        assertTrue((boolean)more.eval(env));
    }

    @Test
    public void testCarCons() throws ParsingException {
        assertEquals(1, (int)(fromString("(car (cons 1 2))").eval(env)));
    }

    @Test
    public void testCdrCons() throws ParsingException {
        assertEquals(2, (int)(fromString("(cdr (cons 1 2))").eval(env)));
    }

    @Test
    public void testAnd() throws ParsingException {
        assertEquals((int)(fromString("(and 1 2)").eval(env)), 2);
        assertInstanceOf(AndEvaluable.class, fromString("(and 1 2)"));
    }

    @Test
    public void testOr() throws ParsingException {
        assertEquals(2, (int)(fromString("(or 2 3)").eval(env)));
        assertInstanceOf(OrEvaluable.class, fromString("(or 2 3)"));
    }
}
