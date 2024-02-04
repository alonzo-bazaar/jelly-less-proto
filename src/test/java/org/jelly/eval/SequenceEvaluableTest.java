package org.jelly.eval;

import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.UndefinedValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.jelly.lang.errors.ParsingException;

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
}
