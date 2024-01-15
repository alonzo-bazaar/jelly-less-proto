package org.jelly.lang;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArithTest {
    @Test
    public void testAdd() {
        assertEquals(2, (int)Arith.add(1,1));
        assertEquals(0, (int)Arith.add(1,-1));
        assertEquals(2.000, (double)Arith.add(1.0,1.0), 0.00001);
        assertEquals(0.000, (double)Arith.add(1.0,-1.0), 0.00001);
    }

    @Test
    public void testSub() {
        assertEquals((int)Arith.subtract(1,1), 0);
        assertEquals((int)Arith.subtract(1,-1), 2);
        assertEquals(0, (double)Arith.subtract(1.0,1.0), 0.00001);
        assertEquals(2, (double)Arith.subtract(1.0,-1.0), 0.00001);
    }

    @Test
    public void testMul() {
        assertEquals(3, (int)Arith.multiply(1,3));
        assertEquals(1, (int)Arith.multiply(1,1));
        assertEquals(-3, (int)Arith.multiply(3,-1));
        assertEquals(-3, (int)Arith.multiply(1,-3));
        assertEquals(2.0, (double)Arith.multiply(1.0,2.0), 0.00001);
        assertEquals(-2.0, (double)Arith.multiply(-1,2.0), 0.00001);
        assertEquals(-1.0, (double)Arith.multiply(1.0,-1.0), 0.00001);
    }

    @Test
    public void testDiv() {
        assertEquals(0.333333, (double)Arith.divide(1,3), 0.00001);
        assertEquals(3.000000, (double)Arith.divide(3,1), 0.00001);
        assertEquals(-0.33333, (double)Arith.divide(-1,3), 0.00001);
        assertEquals(-0.33333, (double)Arith.divide(1,-3), 0.00001);
        assertEquals(0.500000, (double)Arith.divide(1.0,2.0), 0.00001);
        assertEquals(-1.00000, (double)Arith.divide(1.0,-1.0), 0.00001);
    }

    @Test
    public void testGreater() {
        assertTrue(Arith.greaterThan(3,1));
        assertTrue(Arith.greaterThan(3.1,1));
        assertTrue(Arith.greaterThan(0.1,0));
        assertTrue(Arith.greaterThan(0.1,0.0));
    }

    @Test
    public void testLess() {
        assertTrue(Arith.lessThan(1,3));
        assertTrue(Arith.lessThan(1,3.1));
        assertTrue(Arith.lessThan(0,0.1));
        assertTrue(Arith.lessThan(0.0,0.1));
    }

    @Test
    public void testGreaterEqual() {
        assertTrue(Arith.greaterEqual(3,1));
        assertTrue(Arith.greaterEqual(3.1,1));
        assertTrue(Arith.greaterEqual(0.1,0));
        assertTrue(Arith.greaterEqual(0.1,0.0));

        assertTrue(Arith.lessEqual(1,1));
        assertTrue(Arith.lessEqual(0,0));
        assertTrue(Arith.lessEqual(0.1,0.1));
        assertTrue(Arith.lessEqual(0.0,0.0));
    }

    @Test
    public void testLessEqual() {
        assertTrue(Arith.lessEqual(1,3));
        assertTrue(Arith.lessEqual(1,3.1));
        assertTrue(Arith.lessEqual(0,0.1));
        assertTrue(Arith.lessEqual(0.0,0.1));

        assertTrue(Arith.lessEqual(1,1));
        assertTrue(Arith.lessEqual(0,0));
        assertTrue(Arith.lessEqual(0.1,0.1));
        assertTrue(Arith.lessEqual(0.0,0.0));
    }
}
