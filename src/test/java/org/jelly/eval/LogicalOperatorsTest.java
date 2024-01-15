package org.jelly.eval;

import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.EvaluableCreator;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.runtime.Runtime;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.jelly.parse.ExpressionIterator;
import org.jelly.lang.errors.ParsingException;
import org.jelly.lang.Constants;
import org.jelly.lang.LispSymbol;


public class LogicalOperatorsTest {
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
    public void testAndPositive() throws ParsingException {
        assertEquals(10, (int)fromString("(and #t nil 10)").eval(env));
    }

    @Test
    public void testOrPositive() throws ParsingException {
        assertEquals(Constants.NIL, fromString("(or nil #t 10)").eval(env));
        assertEquals(10, (int)fromString("(or 10 nil #t 10)").eval(env));
    }

    @Test
    public void testAndEmpty() throws ParsingException {
        assertEquals(Constants.TRUE, fromString("(and)").eval(env));
    }

    @Test
    public void testOrEmpty() throws ParsingException {
        assertEquals(Constants.FALSE, fromString("(or)").eval(env));
    }

    @Test
    public void testAndSinglePositive() throws ParsingException {
        assertEquals(1, (int)fromString("(and 1)").eval(env));
    }

    @Test
    public void testOrSinglePositive() throws ParsingException {
        assertEquals(1, (int)fromString("(or 1)").eval(env));
    }

    @Test
    public void testAndSingleNegative() throws ParsingException {
        assertEquals(Constants.FALSE, fromString("(and #f)").eval(env));
    }

    @Test
    public void testOrSingleNegative() throws ParsingException {
        assertEquals(Constants.FALSE, fromString("(or #f)").eval(env));
    }

    @Test
    public void testAndNegative() throws ParsingException {
        assertEquals(Constants.FALSE, fromString("(and #t nil #f)").eval(env));
    }

    @Test
    public void testOrNegative() throws ParsingException {
        assertEquals(Constants.FALSE, fromString("(and #f #t nil 10)").eval(env));
        assertEquals(Constants.FALSE, fromString("(and #t #f nil 10)").eval(env));
        assertEquals(Constants.FALSE, fromString("(and #t nil #f 10)").eval(env));
        assertEquals(Constants.FALSE, fromString("(and #t nil 10 #f)").eval(env));
    }

    @Test
    public void testAndShortCircuits() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(and #t (set! a 20))").eval(env);
        assertEquals(20, env.lookup(new LispSymbol("a")));

    }

    @Test
    public void testAndContinues() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(and #f (set! a 20))").eval(env);
        assertEquals(10, env.lookup(new LispSymbol("a")));
    }

    @Test
    public void testOrShortCircuits() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(or #f (set! a 20))").eval(env);
        assertEquals(20, env.lookup(new LispSymbol("a")));
    }

    @Test
    public void testOrContinues() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(or #t (set! a 20))").eval(env);
        assertEquals(10, env.lookup(new LispSymbol("a")));
    }
}
