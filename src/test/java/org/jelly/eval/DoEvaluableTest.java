package org.jelly.eval;

import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.EvaluableCreator;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.runtime.Runtime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.Assert.assertEquals;

import org.jelly.lang.errors.ParsingException;
import org.jelly.parse.ExpressionIterator;

public class DoEvaluableTest {
    private Environment env;

    @BeforeEach
    public void envSetup() {
        env = Runtime.buildInitialEnvironment();
    }

    @AfterEach
    public void envReset() {
        env.reset();
    }

    private Evaluable fromString(String s) throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString(s);
        Object le = ei.next();
        return EvaluableCreator.fromExpression(le);
    }

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
