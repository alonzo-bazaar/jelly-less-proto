package org.jelly.eval.procedure;

import java.util.Arrays;
import java.util.List;

import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.evaluable.procedure.LambdaList;
import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.Constants;
import org.jelly.utils.LispLists;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class LambdaListTest extends BaseEvaluableTest {
    private List<LispSymbol> syms(String... args) {
        return Arrays.stream(args).map(LispSymbol::new).toList();
    }

    private List<Object> vals(Object... args) {
        return Arrays.stream(args).toList();
    }

    @Test
    public void testNormalBinding() {
        LambdaList params = LambdaList.fromList(syms("a", "b", "c"));
        EnvFrame frame = params.bind(vals(10, 20, 30));
        assertEquals(10, frame.lookup(new LispSymbol("a")));
        assertEquals(20, frame.lookup(new LispSymbol("b")));
        assertEquals(30, frame.lookup(new LispSymbol("c")));
    }

    @Test
    public void testEmptyRest() {
        LambdaList params = LambdaList.fromList(syms("a", "b", "c", "&rest", "d"));
        EnvFrame frame = params.bind(vals(10, 20, 30));
        assertEquals(10, frame.lookup(new LispSymbol("a")));
        assertEquals(20, frame.lookup(new LispSymbol("b")));
        assertEquals(30, frame.lookup(new LispSymbol("c")));
        assertSame(Constants.NIL, frame.lookup(new LispSymbol("d")));
    }

    @Test
    public void testFullRest() {
        LambdaList params = LambdaList.fromList(syms("a", "b", "c", "&rest", "d"));
        EnvFrame frame = params.bind(vals(10, 20, 30, 40));
        assertEquals(10, frame.lookup(new LispSymbol("a")));
        assertEquals(20, frame.lookup(new LispSymbol("b")));
        assertEquals(30, frame.lookup(new LispSymbol("c")));

        assertEquals(40, LispLists.nth(frame.lookup(new LispSymbol("d")), 0));
        assertEquals(1, LispLists.requireList(frame.lookup(new LispSymbol("d"))).length());
    }

    @Test
    public void testJustRest() {
        LambdaList params = LambdaList.fromList(syms("&rest", "a"));
        EnvFrame frame = params.bind(vals(10, 'a', 30, "test"));
        assertEquals(10, LispLists.nth(frame.lookup(new LispSymbol("a")),0));
        assertEquals('a', LispLists.nth(frame.lookup(new LispSymbol("a")),1));
        assertEquals(30, LispLists.nth(frame.lookup(new LispSymbol("a")),2));
        assertEquals("test", LispLists.nth(frame.lookup(new LispSymbol("a")),3));

        assertEquals(4, LispLists.requireList(frame.lookup(new LispSymbol("a"))).length());
    }
}
