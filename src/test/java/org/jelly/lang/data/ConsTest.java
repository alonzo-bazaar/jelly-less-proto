package org.jelly.lang.data;

import org.jelly.lang.data.Constants;
import org.jelly.lang.data.NilValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsTest {
    @Test
    public void testLength() {
        Cons a = new Cons(1, Constants.NIL);
        Cons b = new Cons(2, a);
        Cons c = new Cons(3, b);
        assertEquals(1, a.length());
        assertEquals(2, b.length());
        assertEquals(3, c.length());
    }

    @Test
    public void testNthInBounds() {
        Cons a = new Cons(2, new Cons(1, new Cons(0, Constants.NIL)));
        assertEquals(2, a.nth(0));
        assertEquals(1, a.nth(1));
        assertEquals(0, a.nth(2));
    }

    @Test
    public void testNthOutOfBounds() {
        Cons a = new Cons(2, new Cons(1, new Cons(0, Constants.NIL)));
        assertSame(Constants.NIL, a.nth(3));
        assertSame(Constants.NIL, a.nth(4));
    }

    @Test
    public void testNthCdrInBounds() {
        Cons a = new Cons(1, Constants.NIL);
        Cons b = new Cons(2, a);
        Cons c = new Cons(3, b);
        assertSame(c, c.nthCdr(0));
        assertSame(b, c.nthCdr(1));
        assertSame(a, c.nthCdr(2));
    }

    @Test
    public void testNthCdrOutOfBounds() {
        Cons a = new Cons(2, new Cons(1, new Cons(0, Constants.NIL)));
        assertSame(Constants.NIL, a.nthCdr(3));
        assertSame(Constants.NIL, a.nthCdr(4));
    }

    @Test
    public void testLast() {
        Cons a = new Cons(1, Constants.NIL);
        Cons b = new Cons(2, a);
        Cons c = new Cons(3, b);
        assertEquals(a, c.last());
    }
}
