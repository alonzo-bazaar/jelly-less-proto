package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IfEvaluableTest extends BaseEvaluableTest {

    @Test
    public void testTrue() {
        assertEquals(1, (int)eval("(if #t 1 2)"));
    }

    @Test
    public void testStringTrue() {
        assertEquals(1, (int)eval("(if \"test\" 1 2)"));
    }

    @Test
    public void testIfIntTrue() {
        assertEquals(1, (int)eval("(if 0 1 2)"));
    }

    @Test
    public void testFalse() {
        assertEquals(2, (int)eval("(if #f 1 2)"));
    }

    @Test
    public void testNilTrue() {
        assertEquals(1, (int)eval("(if nil 1 2)"));
    }

    @Test
    public void testDoesNotEvalBoth() {
        assertEquals(10, (int)eval("(let ((a 10)) (if #t 10 (set! a 20)) a)"));
        assertEquals(10, (int)eval("(let ((a 10)) (if #f (set! a 20) 10) a)"));
    }
}
