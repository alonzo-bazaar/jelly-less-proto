package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;

import org.jelly.eval.errors.IncorrectArgumentListException;
import org.jelly.eval.errors.IncorrectTypeException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.jelly.parse.errors.ParsingException;


public class ArithFuncallTest extends BaseEvaluableTest {

    @Test
    public void testAdd() throws ParsingException {
        assertEquals(3, (int)eval("(+ 1 2)"));
    }

    @Test
    public void testSub() throws ParsingException {
        assertEquals(-1, eval("(- 1 2)"));
    }

    @Test
    public void testMul() throws ParsingException {
        assertEquals(2, (int)eval("(* 1 2)"));
    }

    @Test
    public void testDiv() throws ParsingException {
        assertEquals(0.5, (double)eval("(/ 1 2)"), 0.00001);
    }

    @Test
    public void testGreater() throws ParsingException {
        assertTrue((boolean)eval("(> 2 1)"));
        assertFalse((boolean)eval("(> 1 1)"));
        assertFalse((boolean)eval("(> 1 2)"));

        assertTrue((boolean)eval("(> 2.0 1)"));
        assertFalse((boolean)eval("(> 1.0 1)"));
        assertFalse((boolean)eval("(> 1.0 2)"));

        assertTrue((boolean)eval("(> 2 1.0)"));
        assertFalse((boolean)eval("(> 1 1.0)"));
        assertFalse((boolean)eval("(> 1 2.0)"));

        assertTrue((boolean)eval("(> 2.0 1.0)"));
        assertFalse((boolean)eval("(> 1.0 1.0)"));
        assertFalse((boolean)eval("(> 1.0 2.0)"));
    }

    @Test
    public void testLess() throws ParsingException {
        assertFalse((boolean)eval("(< 2 1)"));
        assertFalse((boolean)eval("(< 1 1)"));
        assertTrue((boolean)eval("(< 1 2)"));

        assertFalse((boolean)eval("(< 2 1.0)"));
        assertFalse((boolean)eval("(< 1 1.0)"));
        assertTrue((boolean)eval("(< 1 2.0)"));

        assertFalse((boolean)eval("(< 2.0 1)"));
        assertFalse((boolean)eval("(< 1.0 1)"));
        assertTrue((boolean)eval("(< 1.0 2)"));

        assertFalse((boolean)eval("(< 2.0 1.0)"));
        assertFalse((boolean)eval("(< 1.0 1.0)"));
        assertTrue((boolean)eval("(< 1.0 2.0)"));
    }

    @Test
    public void testGreaterEq() throws ParsingException {
        assertTrue((boolean)eval("(>= 2 1)"));
        assertTrue((boolean)eval("(>= 1 1)"));
        assertFalse((boolean)eval("(>= 1 2)"));

        assertTrue((boolean)eval("(>= 2 1.0)"));
        assertTrue((boolean)eval("(>= 1 1.0)"));
        assertFalse((boolean)eval("(>= 1 2.0)"));

        assertTrue((boolean)eval("(>= 2.0 1)"));
        assertTrue((boolean)eval("(>= 1.0 1)"));
        assertFalse((boolean)eval("(>= 1.0 2)"));

        assertTrue((boolean)eval("(>= 2.0 1.0)"));
        assertTrue((boolean)eval("(>= 1.0 1.0)"));
        assertFalse((boolean)eval("(>= 1.0 2.0)"));
    }

    @Test
    public void testLessEq() throws ParsingException {
        assertFalse((boolean)eval("(<= 2 1)"));
        assertTrue((boolean)eval("(<= 1 1)"));
        assertTrue((boolean)eval("(<= 1 2)"));

        assertFalse((boolean)eval("(<= 2 1.0)"));
        assertTrue((boolean)eval("(<= 1 1.0)"));
        assertTrue((boolean)eval("(<= 1 2.0)"));

        assertFalse((boolean)eval("(<= 2.0 1)"));
        assertTrue((boolean)eval("(<= 1.0 1)"));
        assertTrue((boolean)eval("(<= 1.0 2)"));

        assertFalse((boolean)eval("(<= 2.0 1.0)"));
        assertTrue((boolean)eval("(<= 1.0 1.0)"));
        assertTrue((boolean)eval("(<= 1.0 2.0)"));
    }

    @Test
    public void testThrowNonNumericPlus() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            eval("(+ (quote b) 1)");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(+ 1 (quote a))");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(+ (quote b) (quote a))");
        });
    }

    @Test
    public void testThrowNonNumericMinus() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            eval("(- (quote b) 1)");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(- 1 (quote a))");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(- (quote b) (quote a))");
        });
    }

    @Test
    public void testThrowNonNumericMultiply() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            eval("(* (quote b) 1)");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(* 1 (quote a))");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(* (quote b) (quote a))");
        });
    }

    @Test
    public void testThrowNonNumericDivision() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            eval("(/ (quote b) 1)");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(/ 1 (quote a))");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(/ (quote b) (quote a))");
        });
    }

    @Test
    public void testThrowNonNumericComparison() throws ParsingException {
        assertThrows(IncorrectTypeException.class, () -> {
            eval("(> (quote b) 1)");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(> 1 (quote a))");
        });

        assertThrows(IncorrectTypeException.class, () -> {
            eval("(> (quote b) (quote a))");
        });
    }

    @Test
    void throwIfWrongArgSizeComparison() throws ParsingException {
        assertThrows(IncorrectArgumentListException.class, () -> {
            eval("(> 1)");
        });

        assertThrows(IncorrectArgumentListException.class, () -> {
            eval("(> 1 2 3)");
        });

        assertThrows(IncorrectArgumentListException.class, () -> {
            eval("(> 1 2 3 4 5)");
        });
    }

    @Test
    public void testMixedTypesAdd() {
        assertEquals(3.0, (double)eval("(+ 1.0 2)"), 0.00001);
        assertEquals(3.0, (double)eval("(+ 1 2.0)"), 0.00001);
        assertEquals(3.0, (double)eval("(+ 1 1.0 1)"), 0.00001);
    }
}
