package org.jelly.eval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoEvaluableTest extends BaseEvaluableTest {
    @Test
    public void testNormalLoopOneVar() {
        fromString("(define (powah-iter base exp) " +
                   "  (let ((res 1)) " +
                   "    (do ((i 0 (+ i 1))) " +
                   "        ((= i exp)) " +
                   "      (set! res (* res base))) " +
                   "    res)) ").eval(env);
        assertEquals(1024, (int) fromString("(powah-iter 2 10)").eval(env));
        assertEquals(100, (int) fromString("(powah-iter 10 2)").eval(env));
        assertEquals(1, (int) fromString("(powah-iter 10 0)").eval(env));
        assertEquals(0, (int) fromString("(powah-iter 0 10)").eval(env));
        assertEquals(1, (int) fromString("(powah-iter 1 10)").eval(env));
    }

    @Test
    public void testNormalLoopOneVarReturn() {
        assertEquals(20, (int)fromString ("(do ((i 0 (+ i 1))) " +
                                          "((= i 10) (+ i 10)))").eval(env));
    }

    @Test
    public void testVariablesParallelUpdate() {
        assertEquals(50, (int)fromString("(do ((a 10 (+ a 10)) " +
                                         "     (b 0 (+ a 10))) " +
                                         "    ((> a 40) b)) ").eval(env));
    }
}
