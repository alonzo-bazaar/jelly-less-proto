package org.jelly.eval;

import org.jelly.eval.evaluable.AndEvaluable;
import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.EvaluableCreator;
import org.jelly.eval.evaluable.OrEvaluable;
import org.jelly.eval.runtime.Runtime;
import org.jelly.eval.runtime.Environment;
import org.jelly.lang.LispSymbol;
import org.jelly.lang.LispList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.jelly.parse.ExpressionIterator;
import org.jelly.lang.errors.ParsingException;

public class EvaluableCreatorTest {
    private Evaluable fromString(String s) throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString(s);
        Object le = ei.next();
        return EvaluableCreator.fromExpression(le);
    }

    private Environment env = new Environment();

    @BeforeEach
    public void refreshEnv() {
        env = Runtime.buildInitialEnvironment();
    }

    @AfterEach
    public void resetEnvironment() {
        env.reset();
    }

    @Test
    public void testQuoteSymbol() throws ParsingException {
        Evaluable ev = fromString("(quote a)");
        Object o = ev.eval(env);
        assertEquals(((LispSymbol)o).getName(), "a");
    }

    @Test
    public void testQuoteSymbolDoesNotLookup() throws ParsingException {
        Evaluable ev = fromString("(let ((a 10)) (quote a))");
        Object o = ev.eval(env);
        assertEquals(((LispSymbol)o).getName(), "a");
    }

    @Test
    public void testQuoteNumber() throws ParsingException {
        Evaluable ev = fromString("(+ (quote 3) (quote 4))");
        Object o = ev.eval(env);
        assertEquals((int)o, 7);
    }

    @Test
    public void testQuoteString() throws ParsingException {
        Evaluable ev = fromString("(quote \"waluigi\")");
        Object o = ev.eval(env);
        assertEquals((String)o, "waluigi");
    }

    @Test
    public void testQuoteList() throws ParsingException {
        Evaluable ev = fromString("(quote (1 2 3))");
        Object o = ev.eval(env);
        assertEquals((int)((LispList)o).nth(0), 1);
        assertEquals((int)((LispList)o).nth(1), 2);
        assertEquals((int)((LispList)o).nth(2), 3);
    }

    @Test
    public void testQuoteListDynamicallyTypedThing() throws ParsingException {
        Evaluable ev = fromString("(quote (1 \"test\" yee))");
        Object o = ev.eval(env);
        assertEquals((int)((LispList)o).nth(0), 1);
        assertEquals((String)((LispList)o).nth(1), "test");
        assertEquals(((LispSymbol)(((LispList)o).nth(2))).getName(), "yee");
    }

    @Test
    public void testIf() throws ParsingException {
        Evaluable ev = fromString("(if #f 10 20)");
        Object o = ev.eval(env);
        assertEquals((int) o, 20);
    }

    @Test
    public void testWhile() throws ParsingException {
        Evaluable ev = fromString
            ("(let ((a 0) (b 0))" +
             "(while (< a 10) (set! a (+ a 1)) (set! b (+ 2 b)))" +
             "b)");
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
        Evaluable ev = fromString("(let ((a 1)) (begin (set! a 2) a))");
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
    public void testDefineFunctionLambda() throws ParsingException {
        fromString("(define str (lambda (x) (if x \"yes\" \"no\")))").eval(env);
        Object yes = fromString("(str 10)").eval(env);
        Object yesT = fromString("(str #t)").eval(env);
        Object yesNil = fromString("(str nil)").eval(env);
        Object no = fromString("(str #f)").eval(env);

        assertEquals(yes, "yes");
        assertEquals(yesT, "yes");
        assertEquals(yesNil, "yes");
        assertEquals(no, "no");
    }

    @Test
    public void testDefineFunction() throws ParsingException {
        fromString("(define (str x) (if x \"yes\" \"no\"))").eval(env);
        Object yes = fromString("(str 10)").eval(env);
        Object yesT = fromString("(str #t)").eval(env);
        Object yesNil = fromString("(str nil)").eval(env);
        Object no = fromString("(str #f)").eval(env);

        assertEquals(yes, "yes");
        assertEquals(yesT, "yes");
        assertEquals(yesNil, "yes");
        assertEquals(no, "no");
    }

    @Test
    public void testArithCall() throws ParsingException {
        Object o = fromString("(+ 2 3)").eval(env);
        assertEquals((int) o, 5);
    }

    @Test
    public void testArithCallSymbols() throws ParsingException {
        Object o = fromString("(let ((a 2)) (+ a 3))").eval(env);
        assertEquals((int) o, 5);
    }

    @Test
    public void testArithCallSubexpressions() throws ParsingException {
        Object o = fromString("(let ((a 2)) (+ a (+ a 3)))").eval(env);
        assertEquals((int) o, 7);
    }

    @Test
    public void testJustLambda() throws ParsingException {
        Object o = fromString("((lambda (x) x) 3)").eval(env);
        assertEquals((int) o, 3);
    }

    @Test
    public void testLetLambda() throws ParsingException {
        Object o = fromString("(let ((a (lambda (x) x))) (a 3))").eval(env);
        assertEquals((int) o, 3);
    }

    @Test
    public void testFuncallImmediate() throws ParsingException {
        Object o = fromString("((lambda (x y) (+ x (* 2 y))) 1 2)").eval(env);
        assertEquals((int) o, 5);
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
        assertEquals((int)(fromString("(car (cons 1 2))").eval(env)), 1);
    }

    @Test
    public void testCdrCons() throws ParsingException {
        assertEquals((int)(fromString("(cdr (cons 1 2))").eval(env)), 2);
    }

    @Test
    public void testAnd() throws ParsingException {
        assertEquals((int)(fromString("(and 1 2)").eval(env)), 2);
        assertInstanceOf(AndEvaluable.class, fromString("(and 1 2)"));
    }

    @Test
    public void testOr() throws ParsingException {
        assertEquals((int)(fromString("(or 2 3)").eval(env)), 2);
        assertInstanceOf(OrEvaluable.class, fromString("(or 2 3)"));
    }
}
