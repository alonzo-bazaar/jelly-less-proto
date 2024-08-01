package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jelly.parse.errors.ParsingException;
import org.jelly.lang.data.Constants;


public class LogicalOperatorsTest extends BaseEvaluableTest {
    @Test
    public void testAndPositive() throws ParsingException {
        assertEquals(10, (int)eval("(and #t nil 10)"));
    }

    @Test
    public void testOrPositive() throws ParsingException {
        assertEquals(Constants.NIL, eval("(or nil #t 10)"));
        assertEquals(10, (int)eval("(or 10 nil #t 10)"));
    }

    @Test
    public void testAndEmpty() throws ParsingException {
        assertEquals(Constants.TRUE, eval("(and)"));
    }

    @Test
    public void testOrEmpty() throws ParsingException {
        assertEquals(Constants.FALSE, eval("(or)"));
    }

    @Test
    public void testAndSinglePositive() throws ParsingException {
        assertEquals(1, (int)eval("(and 1)"));
    }

    @Test
    public void testOrSinglePositive() throws ParsingException {
        assertEquals(1, (int)eval("(or 1)"));
    }

    @Test
    public void testAndSingleNegative() throws ParsingException {
        assertEquals(Constants.FALSE, eval("(and #f)"));
    }

    @Test
    public void testOrSingleNegative() throws ParsingException {
        assertEquals(Constants.FALSE, eval("(or #f)"));
    }

    @Test
    public void testAndNegative() throws ParsingException {
        assertEquals(Constants.FALSE, eval("(and #t nil #f)"));
    }

    @Test
    public void testOrNegative() throws ParsingException {
        assertEquals(Constants.FALSE, eval("(and #f #t nil 10)"));
        assertEquals(Constants.FALSE, eval("(and #t #f nil 10)"));
        assertEquals(Constants.FALSE, eval("(and #t nil #f 10)"));
        assertEquals(Constants.FALSE, eval("(and #t nil 10 #f)"));
    }

    @Test
    public void testAndShortCircuits() throws ParsingException {
        eval("(define a 10)");
        eval("(and #t (set! a 20))");
        assertEquals(20, lookup("a"));

    }

    @Test
    public void testAndContinues() throws ParsingException {
        eval("(define a 10)");
        eval("(and #f (set! a 20))");
        assertEquals(10, lookup("a"));
    }

    @Test
    public void testOrShortCircuits() throws ParsingException {
        eval("(define a 10)");
        eval("(or #f (set! a 20))");
        assertEquals(20, lookup("a"));
    }

    @Test
    public void testOrContinues() throws ParsingException {
        eval("(define a 10)");
        eval("(or #t (set! a 20))");
        assertEquals(10, lookup("a"));
    }
}
