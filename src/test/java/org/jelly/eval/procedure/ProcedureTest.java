package org.jelly.eval.procedure;

import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.lang.data.LispList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jelly.parse.errors.ParsingException;

public class ProcedureTest extends BaseEvaluableTest {
    @Test
    public void testLambda() throws ParsingException {
        eval("(define bigger-than-10" +
                   "(lambda (x) (if (> x 10) \"yes\" \"no\")))");

        assertEquals("no", (String)eval("(bigger-than-10 9)"));
        assertEquals("no", (String)eval("(bigger-than-10 10)"));
        assertEquals("yes", (String)eval("(bigger-than-10 11)"));
    }

    @Test
    public void testLambdaImmediate() throws ParsingException {
        assertEquals("no", (String)eval("((lambda (x) (if (> x 10) \"yes\" \"no\")) 9)"));

        assertEquals("no", (String)eval("((lambda (x) (if (> x 10) \"yes\" \"no\")) 10)"));

        assertEquals("yes", (String)eval("((lambda (x) (if (> x 10) \"yes\" \"no\")) 11)"));
    }

    @Test
    public void testRecursiveProcedure() throws ParsingException {
        eval("(define fib" +
                "(lambda (x) (if (>= 1 x) 1 " +
                "(+ (fib (- x 1)) (fib (- x 2))))))");


        assertEquals(1, (int)eval("(fib -1)"));
        assertEquals(1, (int)eval("(fib 0)"));
        assertEquals(1, (int)eval("(fib 1)"));
        assertEquals(2, (int)eval("(fib 2)"));
        assertEquals(3, (int)eval("(fib 3)"));
        assertEquals(5, (int)eval("(fib 4)"));
        assertEquals(8, (int)eval("(fib 5)"));
        assertEquals(13, (int)eval("(fib 6)"));
    }

    @Test
    public void testHigherOrderProcedureFunctionParameter() throws ParsingException {
        eval("(define twice (lambda (fn x) (fn (fn x))))");
        eval("(define square (lambda (x) (* x x)))");

        assertEquals(16, (int)eval("(square (square 2))"));
        assertEquals(16, (int)eval("(twice square 2)"));
        assertEquals("ok", 
                     ((String)eval("(cdr (cdr (cons 1 (cons 2 \"ok\"))))")));
        assertEquals("ok", 
                     ((String)eval("(twice cdr (cons 1 (cons 2 \"ok\")))")));
    }

    @Test
    public void testHigherOrderProcedureFunctionParameter_parameterDefinedBefore() throws ParsingException {
        eval("(define square (lambda (x) (* x x)))");
        eval("(define twice (lambda (fn x) (fn (fn x))))");

        assertEquals(16, (int)eval("(twice square 2)"));
        assertEquals("ok", 
                     ((String)eval("(twice cdr (cons 1 (cons 2 \"ok\")))")));
    }


    @Test
    public void testHigherOrderProcedureFunctionReturn() throws ParsingException {
        eval("(define twice (lambda (fn) (lambda (x) (fn (fn x)))))");
        eval("(define square (lambda (x) (* x x)))");

        assertEquals(16, (int)eval("((twice square) 2)"));
        assertEquals("ok",
                     (String)eval("((twice cdr) (cons 1 (cons 2 \"ok\")))"));
    }

    @Test
    public void testHigherOrderProcedureFunctionReturn_parameterDefinedBefore() throws ParsingException {
        eval("(define square (lambda (x) (* x x)))");
        eval("(define twice (lambda (fn) (lambda (x) (fn (fn x)))))");

        assertEquals(16, (int)eval("((twice square) 2)"));
        assertEquals("ok",
                     (String)eval("((twice cdr) (cons 1 (cons 2 \"ok\")))"));
    }

    @Test
    public void testHigherOrderRecursiveProcedure() throws ParsingException {
        eval("(define -map " +
                   "(lambda (fn lst) " +
                   "(if (null? lst) nil " +
                   "(cons (fn (car lst)) (-map fn (cdr lst))))))");
        eval("(define square (lambda (x) (* x x)))");

        Object lst = eval("(map square (quote (1 2 3)))");
        assertEquals(1, (int)((LispList)lst).nth(0));
        assertEquals(4, (int)((LispList)lst).nth(1));
        assertEquals(9, (int)((LispList)lst).nth(2));
    }

    @Test
    public void testCallNoParams() throws ParsingException {
        eval("(define (noparam) (+ 1 2))");
        assertEquals(3, (int)eval("(noparam)"));
    }

    @Test
    public void testJustRest() throws ParsingException {
        eval("(define (-length lst) (if (null? lst) 0 (+ 1 (-length (cdr lst)))))");
        eval("(define (nargs &rest args) (length args))");
        assertEquals(0, (int)eval("(nargs)"));
        assertEquals(1, (int)eval("(nargs 0)"));
        assertEquals(2, (int)eval("(nargs 0 0)"));
    }
}
