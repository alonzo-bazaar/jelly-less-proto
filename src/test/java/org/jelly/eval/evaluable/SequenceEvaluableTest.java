package org.jelly.eval.evaluable;

import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.UndefinedValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SequenceEvaluableTest extends BaseEvaluableTest {
    @Test
    public void testEmptySequenceIsUndefined() {
        assertInstanceOf(UndefinedValue.class, eval("(begin)"));
    }

    @Test
    public void test_whenBeginIsBound_emptySequenceIsUndefined() {
        // weird bug because I forgot to specify a cdr
        define("begin", 5);
        assertInstanceOf(UndefinedValue.class, eval("(begin)"));
    }

    @Test
    public void testUnarySequence() {
        assertEquals(5, (int)eval("(begin 5)"));
    }

    @Test
    public void testLastValue() {
        assertEquals(5, (int)eval("(begin 4 5)"));
    }

    @Test
    public void testLastValueSideEffects() {
        define("a", 10);
        assertEquals(15, (int)eval("(begin (set! a (+ a 5)) a)"));
    }
}
