package org.jelly.eval.evaluable;

import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.UndefinedValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SequenceEvaluableTest extends BaseEvaluableTest {
    @Test
    public void testEmptySequenceIsUndefined() {
        assertInstanceOf(UndefinedValue.class, fromString("(begin)").eval(env));
    }

    @Test
    public void test_whenBeginIsBound_emptySequenceIsUndefined() {
        // weird bug because I forgot to specify a cdr
        env.define(new LispSymbol("begin"), 5);
        assertInstanceOf(UndefinedValue.class, fromString("(begin)").eval(env));
    }

    @Test
    public void testUnarySequence() {
        assertEquals(5, (int)fromString("(begin 5)").eval(env));
    }

    @Test
    public void testLastValue() {
        assertEquals(5, (int)fromString("(begin 4 5)").eval(env));
    }

    @Test
    public void testLastValueSideEffects() {
        env.define(new LispSymbol("a"), 10);
        assertEquals(15, (int)fromString("(begin (set! a (+ a 5)) a)").eval(env));
    }
}
