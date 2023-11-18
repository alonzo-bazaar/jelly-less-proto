package lang;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArithTest {
    @Test
    public void testAdd() {
        assertEquals((int)Arith.add(1,1), 2);
        assertEquals((int)Arith.add(1,-1), 0);
        assertEquals((double)Arith.add(1.0,1.0), 2, 0.00001);
        assertEquals((double)Arith.add(1.0,-1.0), 0, 0.00001);
    }

    @Test
    public void testSub() {
        assertEquals((int)Arith.subtract(1,1), 0);
        assertEquals((int)Arith.subtract(1,-1), 2);
        assertEquals((double)Arith.subtract(1.0,1.0), 0, 0.00001);
        assertEquals((double)Arith.subtract(1.0,-1.0), 2, 0.00001);
    }

    @Test
    public void testMul() {
        assertEquals((int)Arith.multiply(1,3), 3);
        assertEquals((int)Arith.multiply(1,1), 1);
        assertEquals((int)Arith.multiply(3,-1), -3);
        assertEquals((int)Arith.multiply(1,-3), -3);
        assertEquals((double)Arith.multiply(1.0,2.0), 2.0, 0.00001);
        assertEquals((double)Arith.multiply(-1,2.0), -2.0, 0.00001);
        assertEquals((double)Arith.multiply(1.0,-1.0), -1.0, 0.00001);
    }

    @Test
    public void testDiv() {
        assertEquals((double)Arith.divide(1,3), 0.3333333, 0.000001);
        assertEquals((double)Arith.divide(3,1), 3);
        assertEquals((double)Arith.divide(-1,3), -0.3333333, 0.000001);
        assertEquals((double)Arith.divide(1,-3), -0.3333333, 0.000001);
        assertEquals((double)Arith.divide(1.0,2.0), 0.5, 0.00001);
        assertEquals((double)Arith.divide(1.0,-1.0), -1.0, 0.00001);
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
