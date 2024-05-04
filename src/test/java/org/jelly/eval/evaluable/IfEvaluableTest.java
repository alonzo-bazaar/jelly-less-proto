package org.jelly.eval.evaluable;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.Assert.assertEquals;

import org.jelly.lang.data.Constants;

public class IfEvaluableTest extends BaseEvaluableTest {

    @Test
    public void testTrue() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.TRUE),
                                            new ConstantEvaluable((1)),
                                            new ConstantEvaluable((2)));
        assertEquals(1, (int)(iffer.eval(env)));
    }

    @Test
    public void testStringTrue() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable("stringa"),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals(1, (int)(iffer.eval(env)));
    }

    @Test
    public void testIfIntTrue() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(0),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals(1, (int)(iffer.eval(env)));
    }

    @Test
    public void testFalse() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.FALSE),
                                            new ConstantEvaluable((1)),
                                            new ConstantEvaluable((2)));
        assertEquals(2, (int)(iffer.eval(env)));
    }

    @Test
    public void testNilTrue() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.NIL),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals(1, (int)(iffer.eval(env)));
    }
}
