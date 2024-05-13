package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhileLoopEvaluableTest extends BaseEvaluableTest {

    @Test
    public void testNeverRuns() {
        assertEquals(10, (int)fromString( "(let ((a 10))" +
                "(while (< 30 20)" +
                "(set! a (+ 1 a)))" +
                "a)").eval(env));
    }

    @Test
    public void testRunsWhile() {
        assertEquals(20, (int)fromString( "(let ((a 10))" +
                "(while (< a 20)" +
                "(set! a (+ 1 a)))" +
                "a)").eval(env));
    }
}