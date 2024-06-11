package org.jelly.utils;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.LispList;
import org.jelly.utils.ListBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ListBuilderTest {
    @Test
    public void testEmpty() {
        ListBuilder ll = new ListBuilder();
        assertSame(Constants.NIL, ll.get());
    }

    @Test
    public void testAddLastOne() {
        ListBuilder ll = new ListBuilder();
        ll.addLast(10);
        LispList o = ll.get();
        assertInstanceOf(Cons.class, o);
        assertEquals((int) o.getCar(), 10);
        assertSame(o.getCdr(), Constants.NIL);
    }

    @Test
    public void testAddFirstOne() {
        ListBuilder ll = new ListBuilder();
        ll.addFirst(10);
        LispList o = ll.get();
        assertInstanceOf(Cons.class, o);
        assertEquals((int) o.getCar(), 10);
        assertSame(o.getCdr(), Constants.NIL);
    }

    @Test
    public void testAddLastMultiple() {
        ListBuilder ll = new ListBuilder();
        ll.addLast(10);
        ll.addLast(20);
        ll.addLast(30);
        LispList o = ll.get();
        assertInstanceOf(Cons.class, o);
        assertEquals((int) o.nth(0), 10);
        assertEquals((int) o.nth(1), 20);
        assertEquals((int) o.nth(2), 30);
        assertInstanceOf(Cons.class, o);
        assertEquals((int) o.getCar(), 10);
        assertSame(o.nthCdr(3), Constants.NIL);
    }

    @Test
    public void testAddFirstMultiple() {
        ListBuilder ll = new ListBuilder();
        ll.addFirst(10);
        ll.addFirst(20);
        ll.addFirst(30);
        LispList o = ll.get();
        assertInstanceOf(Cons.class, o);
        assertEquals((int) o.nth(0), 30);
        assertEquals((int) o.nth(1), 20);
        assertEquals((int) o.nth(2), 10);
        assertSame(o.nthCdr(3), Constants.NIL);
    }

    @Test
    public void testAddFirstThenLast() {
        ListBuilder ll = new ListBuilder();
        ll.addFirst(10);
        ll.addFirst(20);
        ll.addLast(0);
        LispList o = ll.get();
        assertInstanceOf(Cons.class, o);
        assertEquals((int) o.nth(0), 20);
        assertEquals((int) o.nth(1), 10);
        assertEquals((int) o.nth(2), 0);
        assertSame(o.nthCdr(3), Constants.NIL);
    }

    @Test
    public void testAddLastThenFirst() {
        ListBuilder ll = new ListBuilder();
        ll.addLast(10);
        ll.addLast(20);
        ll.addFirst(0);
        LispList o = ll.get();
        assertInstanceOf(Cons.class, o);
        assertEquals((int) o.nth(0), 0);
        assertEquals((int) o.nth(1), 10);
        assertEquals((int) o.nth(2), 20);
        assertSame(o.nthCdr(3), Constants.NIL);
    }
}
