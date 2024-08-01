package org.jelly.lang.data;

import org.jelly.utils.ListBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

public class ConsInHashMapTest {
    @Test
    public void testListList() {
        Map<Cons, Integer> m = new HashMap<>();
        Cons c = (Cons)new ListBuilder().addFirst(new Symbol("vada")).addLast(new Symbol("affanculo")).get();
        m.put(c, 10);
        assertEquals(10, m.get(c));
    }

    @Test
    public void testListDifferentListPositive() {
        Map<Cons, Integer> m = new HashMap<>();
        Cons c1 = (Cons)new ListBuilder().addFirst(new Symbol("vada")).addLast(new Symbol("affanculo")).get();
        Cons c2 = (Cons)new ListBuilder().addFirst(new Symbol("vada")).addLast(new Symbol("affanculo")).get();
        m.put(c1, 10);
        assertEquals(10, m.get(c2));
    }

    @Test
    public void testListDifferentListNegative() {
        Map<Cons, Integer> m = new HashMap<>();
        Cons c1 = (Cons)new ListBuilder().addFirst(new Symbol("vada")).addLast(new Symbol("affanculo")).get();
        Cons c2 = (Cons)new ListBuilder().addFirst(new Symbol("vada")).addLast(new Symbol("accagare")).get();
        m.put(c1, 10);
        assertNull(m.get(c2));
    }
}
