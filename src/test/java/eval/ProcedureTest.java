package eval;

import lang.Cons;
import lang.LispList;
import org.junit.jupiter.api.Test;

import static eval.EvaluableCreator.fromList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import lang.LispSymbol;
import lang.Constants;
import parse.ExpressionIterator;
import parse.ParsingException;

public class ProcedureTest {
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
    public void testLambdaUsingBuiltins() throws ParsingException {
        fromString("(define bigger-than-10" +
                   "(lambda (x) (if (> x 10) \"yes\" \"no\")))").eval(env);

        assertEquals((String)fromString("(bigger-than-10 9)").eval(env), "no");
        assertEquals((String)fromString("(bigger-than-10 10)").eval(env), "no");
        assertEquals((String)fromString("(bigger-than-10 11)").eval(env), "yes");
    }

    @Test
    public void testLambdaImmediateBuiltins() throws ParsingException {
        assertEquals((String)fromString
                     ("((lambda (x) (if (> x 10) \"yes\" \"no\")) 9)")
                     .eval(env), "no");

        assertEquals((String)fromString
                     ("((lambda (x) (if (> x 10) \"yes\" \"no\")) 10)")
                     .eval(env), "no");

        assertEquals((String)fromString
                     ("((lambda (x) (if (> x 10) \"yes\" \"no\")) 11)")
                     .eval(env), "yes");
    }

    @Test
    public void testRecursiveProcedure() throws ParsingException {
        fromString("(define fib" +
                "(lambda (x) (if (>= 1 x) 1 " +
                "(+ (fib (- x 1)) (fib (- x 2))))))").eval(env);


        assertEquals((int)fromString("(fib -1)").eval(env), 1);
        assertEquals((int)fromString("(fib 0)").eval(env), 1);
        assertEquals((int)fromString("(fib 1)").eval(env), 1);
        assertEquals((int)fromString("(fib 2)").eval(env), 2);
        assertEquals((int)fromString("(fib 3)").eval(env), 3);
        assertEquals((int)fromString("(fib 4)").eval(env), 5);
        assertEquals((int)fromString("(fib 5)").eval(env), 8);
        assertEquals((int)fromString("(fib 6)").eval(env), 13);
    }

    @Test
    public void testHigherOrderProcedureParametersDefinedAfter() throws ParsingException {
        fromString("(define twice (lambda (fn x) (fn (fn x))))").eval(env);
        fromString("(define square (lambda (x) (* x x)))").eval(env);

        assertEquals((int)fromString("(square (square 2))").eval(env), 16);
        assertEquals((int)fromString("(twice square 2)").eval(env), 16);
        assertEquals
                ((String)fromString("(cdr (cdr (cons 1 (cons 2 \"ok\"))))").eval(env),
                        "ok");
        assertEquals
            ((String)fromString("(twice cdr (cons 1 (cons 2 \"ok\")))").eval(env),
             "ok");
    }

    @Test
    public void testHigherOrderProcedureParametersDefinedBefore() throws ParsingException {
        fromString("(define square (lambda (x) (* x x)))").eval(env);
        fromString("(define twice (lambda (fn x) (fn (fn x))))").eval(env);

        assertEquals((int)fromString("(twice square 2)").eval(env), 16);
        assertEquals
                ((String)fromString("(twice cdr (cons 1 (cons 2 \"ok\")))").eval(env),
                        "ok");
    }


    @Test
    public void testHigherOrderProcedureLambdaDefinedBefore() throws ParsingException {
        fromString("(define square (lambda (x) (* x x)))").eval(env);
        fromString("(define twice (lambda (fn) (lambda (x) (fn (fn x)))))").eval(env);

        assertEquals((int)fromString("((twice square) 2)").eval(env), 16);
        assertEquals
            ((String)fromString("((twice cdr) (cons 1 (cons 2 \"ok\")))").eval(env),
             "ok");
    }
    @Test
    public void testHigherOrderProcedureLambdaDefinedAfter() throws ParsingException {
        fromString("(define twice (lambda (fn) (lambda (x) (fn (fn x)))))").eval(env);
        fromString("(define square (lambda (x) (* x x)))").eval(env);

        assertEquals((int)fromString("((twice square) 2)").eval(env), 16);
        assertEquals
            ((String)fromString("((twice cdr) (cons 1 (cons 2 \"ok\")))").eval(env),
             "ok");
    }

    @Test
    public void testHigherOrderRecursiveProcedure() throws ParsingException {
        fromString("(define map " +
                   "(lambda (fn lst) " +
                   "(if (null lst) nil " +
                   "(cons (fn (car lst)) (map fn (cdr lst))))))").eval(env);
        fromString("(define square (lambda (x) (* x x)))").eval(env);

        Object lst = fromString("(map square (quote (1 2 3)))").eval(env);
        assertEquals((int)((LispList)lst).nth(0), 1);
        assertEquals((int)((LispList)lst).nth(1), 4);
        assertEquals((int)((LispList)lst).nth(2), 9);
    }
}
