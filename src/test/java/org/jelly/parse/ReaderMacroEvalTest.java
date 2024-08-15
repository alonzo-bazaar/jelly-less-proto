package org.jelly.parse;

import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.ConsUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReaderMacroEvalTest extends BaseEvaluableTest {
    @Test
    public void testSymbol() {
        assertEquals(new Symbol("a"), eval("'a"));
    }

    @Test
    public void testList() {
        assertEquals(ConsUtils.of(new Symbol("wow"), 3, "elements"), eval("'(wow 3 \"elements\")"));
    }

    @Test
    public void testEmptyList() {
        assertSame(Constants.NIL, eval("'()"));
    }

    @Test
    public void testCallSpecialForm() {
        assertEquals(new Symbol("a"), eval("(if #t 'a 'b)"));
        assertEquals(new Symbol("b"), eval("(if #f 'a 'b)"));
    }

    @Test
    public void testCallFun() {
        assertEquals(new Symbol("a"), eval("(id 'a)"));
        assertEquals(ConsUtils.of(new Symbol("a"), new Symbol("b"), new Symbol("c")),
                eval("(list 'a 'b 'c)"));
    }
}
