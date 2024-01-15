package org.jelly.utils;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IteratorCollectorTest {
    /* prefix iterator tests */
    @Test
    public void arrayListCollect() {
        ArrayList<Character> al = IteratorCollector.collectToArray(new StringCharIterator("cristo"));
        assertEquals('c', (char)al.get(0));
        assertEquals('r', (char)al.get(1));
        assertEquals('i', (char)al.get(2));
        assertEquals('s', (char)al.get(3));
        assertEquals('t', (char)al.get(4));
        assertEquals('o', (char)al.get(5));
    }

    @Test
    public void linkedListCollect() {
        LinkedList<Character> li = IteratorCollector.collectToLinked(new StringCharIterator("cristo"));
        assertEquals('c', (char)li.get(0));
        assertEquals('r', (char)li.get(1));
        assertEquals('i', (char)li.get(2));
        assertEquals('s', (char)li.get(3));
        assertEquals('t', (char)li.get(4));
        assertEquals('o', (char)li.get(5));
    }

    @Test
    public void stringCollect() {
        String str = IteratorCollector.collectToString(new StringCharIterator("cristo"));
        assertTrue(str.equals("cristo"));
    }
}
