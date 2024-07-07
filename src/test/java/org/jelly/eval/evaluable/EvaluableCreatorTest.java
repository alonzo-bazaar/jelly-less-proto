package org.jelly.eval.evaluable;

import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.LispList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.jelly.parse.errors.ParsingException;

public class EvaluableCreatorTest extends BaseEvaluableTest {
    @Test
    public void testQuoteSymbol() throws ParsingException {
        assertEquals("a", ((LispSymbol)eval("(quote a)")).getName());
    }

    @Test
    public void testQuoteSymbolDoesNotLookup() throws ParsingException {
        assertEquals("a",
                     ((LispSymbol)eval("(let ((a 10)) (quote a))")).getName());
    }

    @Test
    public void testQuoteNumber() throws ParsingException {
        assertEquals(7, eval("(+ (quote 3) (quote 4))"));
    }

    @Test
    public void testQuoteString() throws ParsingException {
        assertEquals("waluigi", eval("(quote \"waluigi\")"));
    }

    @Test
    public void testQuoteList() throws ParsingException {
        Object o = eval("(quote (1 2 3))");
        assertEquals(1, (int)((LispList)o).nth(0));
        assertEquals(2, (int)((LispList)o).nth(1));
        assertEquals(3, (int)((LispList)o).nth(2));
    }

    @Test
    public void testQuoteListDynamicallyTypedThing() throws ParsingException {
        Object o = eval("(quote (1 \"test\" yee))");
        assertEquals(1, (int)((LispList)o).nth(0));
        assertEquals("test", (String)((LispList)o).nth(1));
        assertEquals("yee", ((LispSymbol)(((LispList)o).nth(2))).getName());
    }

    @Test
    public void testIf() throws ParsingException {
        Object o = eval("(if #f 10 20)");
        assertEquals(20, (int) o);
    }

    @Test
    public void testWhile() throws ParsingException {
        Object o = eval ("(let ((a 0) (b 0))" +
                         "(while (< a 10) (set! a (+ a 1)) (set! b (+ 2 b)))" +
                         "b)");
        assertEquals(20, (int) o);
    }

    @Test
    public void testSequenceReturn() throws ParsingException {
        Object o = eval("(begin 10 20 30)");
        assertEquals(30, (int) o);
    }

    @Test
    public void testSequenceSideEffect() throws ParsingException {
        Object o = eval("(let ((a 1)) (begin (set! a 2) a))");
        assertEquals(2, (int) o);
    }

    @Test
    public void testDefineVariable() throws ParsingException {
        eval("(define x 10)");
        Object o = eval("x");
        assertEquals(10, (int) o);
    }

    @Test
    public void testDefineFunctionLambda() throws ParsingException {
        eval("(define str (lambda (x) (if x \"yes\" \"no\")))");
        Object yes = eval("(str 10)");
        Object yesT = eval("(str #t)");
        Object yesNil = eval("(str nil)");
        Object no = eval("(str #f)");

        assertEquals("yes", yes);
        assertEquals("yes", yesT);
        assertEquals("yes", yesNil);
        assertEquals("no", no);
    }

    @Test
    public void testDefineFunction() throws ParsingException {
        eval("(define (str x) (if x \"yes\" \"no\"))");
        Object yes = eval("(str 10)");
        Object yesT = eval("(str #t)");
        Object yesNil = eval("(str nil)");
        Object no = eval("(str #f)");

        assertEquals("yes", yes);
        assertEquals("yes", yesT);
        assertEquals("yes", yesNil);
        assertEquals("no", no);
    }

    @Test
    public void testArithCall() throws ParsingException {
        Object o = eval("(+ 2 3)");
        assertEquals(5, (int) o);
    }

    @Test
    public void testArithCallSymbols() throws ParsingException {
        Object o = eval("(let ((a 2)) (+ a 3))");
        assertEquals(5, (int) o);
    }

    @Test
    public void testArithCallSubexpressions() throws ParsingException {
        Object o = eval("(let ((a 2)) (+ a (+ a 3)))");
        assertEquals(7, (int) o);
    }

    @Test
    public void testJustLambda() throws ParsingException {
        Object o = eval("((lambda (x) x) 3)");
        assertEquals(3, (int) o);
    }

    @Test
    public void testLetLambda() throws ParsingException {
        Object o = eval("(let ((a (lambda (x) x))) (a 3))");
        assertEquals(3, (int) o);
    }

    @Test
    public void testFuncallImmediate() throws ParsingException {
        Object o = eval("((lambda (x y) (+ x (* 2 y))) 1 2)");
        assertEquals(5, (int) o);
    }

    @Test
    public void testLessThan() throws ParsingException {
        assertFalse((boolean)eval("(> 10 10.1)"));
    }

    @Test
    public void testMoreThan() throws ParsingException {
        assertTrue((boolean)eval("(< 10 10.1)"));
    }

    @Test
    public void testCarCons() throws ParsingException {
        assertEquals(1, (int)(eval("(car (cons 1 2))")));
    }

    @Test
    public void testCdrCons() throws ParsingException {
        assertEquals(2, (int)(eval("(cdr (cons 1 2))")));
    }
}
