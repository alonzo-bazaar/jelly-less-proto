package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LetEvaluableTest extends BaseEvaluableTest {
    @Test
    public void testOneVarAssigned() {
        assertEquals(10, (int)fromString( "(let ((a 10)) a)").eval(env));
    }

    @Test
    public void testMultipleVarsAssigned() {
        assertEquals(10, (int)fromString( "(let ((a 10) (b 20)) a)").eval(env));
        assertEquals(20, (int)fromString( "(let ((a 10) (b 20)) b)").eval(env));
    }

    @Test
    public void testNestedLetShadows() {
        assertEquals(20, (int)fromString( "(let ((a 10)) (let ((a 20)) a))").eval(env));
    }
    @Test
    public void testNestedLetShadowsLocally() {
        assertEquals(10, (int)fromString( "(let ((a 10)) (let ((a 20))) a)").eval(env));
    }
}
