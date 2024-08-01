package org.jelly.eval.procedure;

import java.util.Arrays;
import java.util.List;

import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.evaluable.procedure.LambdaList;
import org.jelly.lang.data.Symbol;
import org.jelly.lang.data.Constants;
import org.jelly.utils.ConsUtils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class LambdaListTest extends BaseEvaluableTest {
    private List<Symbol> syms(String... args) {
        return Arrays.stream(args).map(Symbol::new).toList();
    }

    private List<Object> vals(Object... args) {
        return Arrays.stream(args).toList();
    }

    @Test
    public void testNormalBinding() {
        LambdaList params = LambdaList.fromList(syms("a", "b", "c"));
        EnvFrame frame = params.bind(vals(10, 20, 30));
        assertEquals(10, frame.lookup(new Symbol("a")));
        assertEquals(20, frame.lookup(new Symbol("b")));
        assertEquals(30, frame.lookup(new Symbol("c")));
    }

    @Test
    public void testEmptyRest() {
        LambdaList params = LambdaList.fromList(syms("a", "b", "c", "&rest", "d"));
        EnvFrame frame = params.bind(vals(10, 20, 30));
        assertEquals(10, frame.lookup(new Symbol("a")));
        assertEquals(20, frame.lookup(new Symbol("b")));
        assertEquals(30, frame.lookup(new Symbol("c")));
        assertSame(Constants.NIL, frame.lookup(new Symbol("d")));
    }

    @Test
    public void testFullRest() {
        LambdaList params = LambdaList.fromList(syms("a", "b", "c", "&rest", "d"));
        EnvFrame frame = params.bind(vals(10, 20, 30, 40));
        assertEquals(10, frame.lookup(new Symbol("a")));
        assertEquals(20, frame.lookup(new Symbol("b")));
        assertEquals(30, frame.lookup(new Symbol("c")));

        assertEquals(40, ConsUtils.nth(frame.lookup(new Symbol("d")), 0));
        assertEquals(1, ConsUtils.requireList(frame.lookup(new Symbol("d"))).length());
    }

    @Test
    public void testJustRest() {
        LambdaList params = LambdaList.fromList(syms("&rest", "a"));
        EnvFrame frame = params.bind(vals(10, 'a', 30, "test"));
        assertEquals(10, ConsUtils.nth(frame.lookup(new Symbol("a")),0));
        assertEquals('a', ConsUtils.nth(frame.lookup(new Symbol("a")),1));
        assertEquals(30, ConsUtils.nth(frame.lookup(new Symbol("a")),2));
        assertEquals("test", ConsUtils.nth(frame.lookup(new Symbol("a")),3));

        assertEquals(4, ConsUtils.requireList(frame.lookup(new Symbol("a"))).length());
    }
}
