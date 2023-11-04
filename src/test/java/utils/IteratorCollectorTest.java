package utils;

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
        assertEquals((char)al.get(0), 'c');
        assertEquals((char)al.get(1), 'r');
        assertEquals((char)al.get(2), 'i');
        assertEquals((char)al.get(3), 's');
        assertEquals((char)al.get(4), 't');
        assertEquals((char)al.get(5), 'o');
    }

    @Test
    public void linkedListCollect() {
        LinkedList<Character> li = IteratorCollector.collectToLinked(new StringCharIterator("cristo"));
        assertEquals((char)li.get(0), 'c');
        assertEquals((char)li.get(1), 'r');
        assertEquals((char)li.get(2), 'i');
        assertEquals((char)li.get(3), 's');
        assertEquals((char)li.get(4), 't');
        assertEquals((char)li.get(5), 'o');
    }

    @Test
    public void stringCollect() {
        String str = IteratorCollector.collectToString(new StringCharIterator("cristo"));
        assertTrue(str.equals("cristo"));
    }
}
