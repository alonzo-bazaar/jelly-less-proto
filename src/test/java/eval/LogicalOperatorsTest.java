package eval;

import org.junit.jupiter.api.Test;

import eval.errors.IncorrectArgumentListException;
import eval.errors.IncorrectTypeException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import parse.ExpressionIterator;
import lang.errors.ParsingException;
import lang.Constants;
import lang.LispSymbol;


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
        assertEquals((int)fromString("(and #t nil 10)").eval(env), 10);
    }

    @Test
    public void testOrPositive() throws ParsingException {
        assertEquals(fromString("(or nil #t 10)").eval(env), Constants.NIL);
        assertEquals((int)fromString("(or 10 nil #t 10)").eval(env), 10);
    }

    @Test
    public void testAndEmpty() throws ParsingException {
        assertEquals(fromString("(and)").eval(env), Constants.TRUE);
    }

    @Test
    public void testOrEmpty() throws ParsingException {
        assertEquals(fromString("(or)").eval(env), Constants.FALSE);
    }

    @Test
    public void testAndSinglePositive() throws ParsingException {
        assertEquals((int)fromString("(and 1)").eval(env), 1);
    }

    @Test
    public void testOrSinglePositive() throws ParsingException {
        assertEquals((int)fromString("(or 1)").eval(env), 1);
    }

    @Test
    public void testAndSingleNegative() throws ParsingException {
        assertEquals(fromString("(and #f)").eval(env), Constants.FALSE);
    }

    @Test
    public void testOrSingleNegative() throws ParsingException {
        assertEquals(fromString("(or #f)").eval(env), Constants.FALSE);
    }

    @Test
    public void testAndNegative() throws ParsingException {
        assertEquals(fromString("(and #t nil #f)").eval(env), Constants.FALSE);
    }

    @Test
    public void testOrNegative() throws ParsingException {
        assertEquals(fromString("(and #f #t nil 10)").eval(env), Constants.FALSE);
        assertEquals(fromString("(and #t #f nil 10)").eval(env), Constants.FALSE);
        assertEquals(fromString("(and #t nil #f 10)").eval(env), Constants.FALSE);
        assertEquals(fromString("(and #t nil 10 #f)").eval(env), Constants.FALSE);
    }

    @Test
    public void testAndShortCircuits() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(and #t (set! a 20))").eval(env);
        assertEquals(env.lookup(new LispSymbol("a")), 20);

    }

    @Test
    public void testAndContinues() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(and #f (set! a 20))").eval(env);
        assertEquals(env.lookup(new LispSymbol("a")), 10);
    }

    @Test
    public void testOrShortCircuits() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(or #f (set! a 20))").eval(env);
        assertEquals(env.lookup(new LispSymbol("a")), 20);
    }

    @Test
    public void testOrContinues() throws ParsingException {
        fromString("(define a 10)").eval(env);
        fromString("(or #t (set! a 20))").eval(env);
        assertEquals(env.lookup(new LispSymbol("a")), 10);
    }
}
