package org.jelly.eval;

import org.junit.jupiter.api.Test;

import org.jelly.eval.errors.IncorrectArgumentListException;
import org.jelly.eval.errors.IncorrectTypeException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.jelly.lang.errors.ParsingException;


public class ArithEvaluableTest extends BaseEvaluableTest {

    @Test
    public void testAdd() throws ParsingException {
        assertEquals(3, (int)fromString("(+ 1 2)").eval(env));
    }

    @Test
    public void testSub() throws ParsingException {
        assertEquals(-1, (int)fromString("(- 1 2)").eval(env));
    }

    @Test
    public void testMul() throws ParsingException {
        assertEquals(2, (int)fromString("(* 1 2)").eval(env));
    }

    @Test
    public void testDiv() throws ParsingException {
        assertEquals(0.5, (double)fromString("(/ 1 2)").eval(env), 0.00001);
    }

    @Test
    public void testGreater() throws ParsingException {
        assertTrue((boolean)fromString("(> 2 1)").eval(env));
        assertFalse((boolean)fromString("(> 1 1)").eval(env));
        assertFalse((boolean)fromString("(> 1 2)").eval(env));

        assertTrue((boolean)fromString("(> 2.0 1)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 1)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 2)").eval(env));

        assertTrue((boolean)fromString("(> 2 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1 2.0)").eval(env));

        assertTrue((boolean)fromString("(> 2.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(> 1.0 2.0)").eval(env));
    }

    @Test
    public void testLess() throws ParsingException {
        assertFalse((boolean)fromString("(< 2 1)").eval(env));
        assertFalse((boolean)fromString("(< 1 1)").eval(env));
        assertTrue((boolean)fromString("(< 1 2)").eval(env));

        assertFalse((boolean)fromString("(< 2 1.0)").eval(env));
        assertFalse((boolean)fromString("(< 1 1.0)").eval(env));
        assertTrue((boolean)fromString("(< 1 2.0)").eval(env));

        assertFalse((boolean)fromString("(< 2.0 1)").eval(env));
        assertFalse((boolean)fromString("(< 1.0 1)").eval(env));
        assertTrue((boolean)fromString("(< 1.0 2)").eval(env));

        assertFalse((boolean)fromString("(< 2.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(< 1.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(< 1.0 2.0)").eval(env));
    }

    @Test
    public void testGreaterEq() throws ParsingException {
        assertTrue((boolean)fromString("(>= 2 1)").eval(env));
        assertTrue((boolean)fromString("(>= 1 1)").eval(env));
        assertFalse((boolean)fromString("(>= 1 2)").eval(env));

        assertTrue((boolean)fromString("(>= 2 1.0)").eval(env));
        assertTrue((boolean)fromString("(>= 1 1.0)").eval(env));
        assertFalse((boolean)fromString("(>= 1 2.0)").eval(env));

        assertTrue((boolean)fromString("(>= 2.0 1)").eval(env));
        assertTrue((boolean)fromString("(>= 1.0 1)").eval(env));
        assertFalse((boolean)fromString("(>= 1.0 2)").eval(env));

        assertTrue((boolean)fromString("(>= 2.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(>= 1.0 1.0)").eval(env));
        assertFalse((boolean)fromString("(>= 1.0 2.0)").eval(env));
    }

    @Test
    public void testLessEq() throws ParsingException {
        assertFalse((boolean)fromString("(<= 2 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1 2)").eval(env));

        assertFalse((boolean)fromString("(<= 2 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1 2.0)").eval(env));

        assertFalse((boolean)fromString("(<= 2.0 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 1)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 2)").eval(env));

        assertFalse((boolean)fromString("(<= 2.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 1.0)").eval(env));
        assertTrue((boolean)fromString("(<= 1.0 2.0)").eval(env));
    }

    @Test
    public void testThrowNonNumericPlus() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(+ (quote b) 1)").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(+ 1 (quote a))").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(+ (quote b) (quote a))").eval(env);
        });
    }

    @Test
    public void testThrowNonNumericMinus() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(- (quote b) 1)").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(- 1 (quote a))").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(- (quote b) (quote a))").eval(env);
        });
    }

    @Test
    public void testThrowNonNumericMultiply() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(* (quote b) 1)").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(* 1 (quote a))").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(* (quote b) (quote a))").eval(env);
        });
    }

    @Test
    public void testThrowNonNumericDivision() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(/ (quote b) 1)").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(/ 1 (quote a))").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(/ (quote b) (quote a))").eval(env);
        });
    }

    @Test
    public void testThrowNonNumericComparison() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(> (quote b) 1)").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(> 1 (quote a))").eval(env);
        });

        assertThrows(IncorrectTypeException.class, () -> {
            fromString("(> (quote b) (quote a))").eval(env);
        });
    }

    @Test
    void throwIfWrongArgSizeComparison() throws ParsingException {
        assertThrows(IncorrectArgumentListException.class, () -> {
            fromString("(> 1)").eval(env);
        });

        assertThrows(IncorrectArgumentListException.class, () -> {
            fromString("(> 1 2 3)").eval(env);
        });

        assertThrows(IncorrectArgumentListException.class, () -> {
            fromString("(> 1 2 3 4 5)").eval(env);
        });
    }

}
