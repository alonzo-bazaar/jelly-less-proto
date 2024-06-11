package org.jelly.eval.procedure;

import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.lang.data.LispList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jelly.parse.errors.ParsingException;

public class ProcedureTest extends BaseEvaluableTest {
    @Test
    public void testLambda() throws ParsingException {
        fromString("(define bigger-than-10" +
                   "(lambda (x) (if (> x 10) \"yes\" \"no\")))").eval(env);

        assertEquals("no", (String)fromString("(bigger-than-10 9)").eval(env));
        assertEquals("no", (String)fromString("(bigger-than-10 10)").eval(env));
        assertEquals("yes", (String)fromString("(bigger-than-10 11)").eval(env));
    }

    @Test
    public void testLambdaImmediate() throws ParsingException {
        assertEquals("no", (String)fromString
                     ("((lambda (x) (if (> x 10) \"yes\" \"no\")) 9)")
                     .eval(env));

        assertEquals("no", (String)fromString
                     ("((lambda (x) (if (> x 10) \"yes\" \"no\")) 10)")
                     .eval(env));

        assertEquals("yes", (String)fromString
                     ("((lambda (x) (if (> x 10) \"yes\" \"no\")) 11)")
                     .eval(env));
    }

    @Test
    public void testRecursiveProcedure() throws ParsingException {
        fromString("(define fib" +
                "(lambda (x) (if (>= 1 x) 1 " +
                "(+ (fib (- x 1)) (fib (- x 2))))))").eval(env);


        assertEquals(1, (int)fromString("(fib -1)").eval(env));
        assertEquals(1, (int)fromString("(fib 0)").eval(env));
        assertEquals(1, (int)fromString("(fib 1)").eval(env));
        assertEquals(2, (int)fromString("(fib 2)").eval(env));
        assertEquals(3, (int)fromString("(fib 3)").eval(env));
        assertEquals(5, (int)fromString("(fib 4)").eval(env));
        assertEquals(8, (int)fromString("(fib 5)").eval(env));
        assertEquals(13, (int)fromString("(fib 6)").eval(env));
    }

    @Test
    public void testHigherOrderProcedureFunctionParameter() throws ParsingException {
        fromString("(define twice (lambda (fn x) (fn (fn x))))").eval(env);
        fromString("(define square (lambda (x) (* x x)))").eval(env);

        assertEquals(16, (int)fromString("(square (square 2))").eval(env));
        assertEquals(16, (int)fromString("(twice square 2)").eval(env));
        assertEquals("ok", 
                     ((String)fromString("(cdr (cdr (cons 1 (cons 2 \"ok\"))))").eval(env)));
        assertEquals("ok", 
                     ((String)fromString("(twice cdr (cons 1 (cons 2 \"ok\")))").eval(env)));
    }

    @Test
    public void testHigherOrderProcedureFunctionParameter_parameterDefinedBefore() throws ParsingException {
        fromString("(define square (lambda (x) (* x x)))").eval(env);
        fromString("(define twice (lambda (fn x) (fn (fn x))))").eval(env);

        assertEquals(16, (int)fromString("(twice square 2)").eval(env));
        assertEquals("ok", 
                     ((String)fromString("(twice cdr (cons 1 (cons 2 \"ok\")))").eval(env)));
    }


    @Test
    public void testHigherOrderProcedureFunctionReturn() throws ParsingException {
        fromString("(define twice (lambda (fn) (lambda (x) (fn (fn x)))))").eval(env);
        fromString("(define square (lambda (x) (* x x)))").eval(env);

        assertEquals(16, (int)fromString("((twice square) 2)").eval(env));
        assertEquals("ok",
                     (String)fromString("((twice cdr) (cons 1 (cons 2 \"ok\")))").eval(env));
    }

    @Test
    public void testHigherOrderProcedureFunctionReturn_parameterDefinedBefore() throws ParsingException {
        fromString("(define square (lambda (x) (* x x)))").eval(env);
        fromString("(define twice (lambda (fn) (lambda (x) (fn (fn x)))))").eval(env);

        assertEquals(16, (int)fromString("((twice square) 2)").eval(env));
        assertEquals("ok",
                     (String)fromString("((twice cdr) (cons 1 (cons 2 \"ok\")))").eval(env));
    }

    @Test
    public void testHigherOrderRecursiveProcedure() throws ParsingException {
        fromString("(define map " +
                   "(lambda (fn lst) " +
                   "(if (null? lst) nil " +
                   "(cons (fn (car lst)) (map fn (cdr lst))))))").eval(env);
        fromString("(define square (lambda (x) (* x x)))").eval(env);

        Object lst = fromString("(map square (quote (1 2 3)))").eval(env);
        assertEquals(1, (int)((LispList)lst).nth(0));
        assertEquals(4, (int)((LispList)lst).nth(1));
        assertEquals(9, (int)((LispList)lst).nth(2));
    }

    @Test
    public void testCallNoParams() throws ParsingException {
        fromString("(define (noparam) (+ 1 2))").eval(env);
        assertEquals(3, (int)fromString("(noparam)").eval(env));
    }

    @Test
    public void testJustRest() throws ParsingException {
        fromString("(define (length lst) (if (null? lst) 0 (+ 1 (length (cdr lst)))))").eval(env);
        fromString("(define (nargs &rest args) (length args))").eval(env);
        assertEquals(0, (int)fromString("(nargs)").eval(env));
        assertEquals(1, (int)fromString("(nargs 0)").eval(env));
        assertEquals(2, (int)fromString("(nargs 0 0)").eval(env));
    }
}
