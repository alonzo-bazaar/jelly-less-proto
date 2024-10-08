package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LetEvaluableTest extends BaseEvaluableTest {
    @Test
    public void testOneVarAssigned() {
        assertEquals(10, (int)eval("(let ((a 10)) a)"));
    }

    @Test
    public void testMultipleVarsAssigned() {
        assertEquals(10, (int)eval("(let ((a 10) (b 20)) a)"));
        assertEquals(20, (int)eval("(let ((a 10) (b 20)) b)"));
    }

    @Test
    public void testNestedLetShadows() {
        assertEquals(20, (int)eval("(let ((a 10)) (let ((a 20)) a))"));
    }

    @Test
    public void testNestedLetShadowsLocally() {
        assertEquals(10, (int)eval("(let ((a 10)) (let ((a 20))) a)"));
    }

    @Test
    public void testRecCall() {
        assertEquals(3,
                eval("(let len ((lst (list 1 2 3))) " +
                        "          (if (null? lst) 0" +
                        "              (+ 1 (len (cdr lst)))))"));
    }

    @Test
    public void testRecCallDoesNotOverride() {
        define("len", 10);
        eval("(let len ((lst (list 1 2 3))) " +
                "          (if (null? lst) 0" +
                "              (+ 1 (len (cdr lst)))))");
        assertEquals(10, lookup("len"));
    }
}
