package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoEvaluableTest extends BaseEvaluableTest {
    @Test
    public void testNormalLoopOneVar() {
        eval("(define (powah-iter base exp) " +
                   "  (let ((res 1)) " +
                   "    (do ((i 0 (+ i 1))) " +
                   "        ((= i exp)) " +
                   "      (set! res (* res base))) " +
                   "    res)) ");
        assertEquals(1024, (int) eval("(powah-iter 2 10)"));
        assertEquals(100, (int) eval("(powah-iter 10 2)"));
        assertEquals(1, (int) eval("(powah-iter 10 0)"));
        assertEquals(0, (int) eval("(powah-iter 0 10)"));
        assertEquals(1, (int) eval("(powah-iter 1 10)"));
    }

    @Test
    public void testNormalLoopOneVarReturn() {
        assertEquals(20, (int)eval ("(do ((i 0 (+ i 1))) " +
                                          "((= i 10) (+ i 10)))"));
    }

    @Test
    public void testVariablesParallelUpdate() {
        assertEquals(50, (int)eval("(do ((a 10 (+ a 10)) " +
                                         "     (b 0 (+ a 10))) " +
                                         "    ((> a 40) b)) "));
    }
}
