package org.jelly.eval;


import org.jelly.eval.evaluable.ConstantEvaluable;
import org.jelly.eval.evaluable.IfEvaluable;
import org.jelly.eval.runtime.Environment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.Assert.assertEquals;

import org.jelly.lang.Constants;

public class IfEvaluableTest {
    private Environment env;
    @BeforeEach
    public void envSetup() {
        env = new Environment();
    }
    @AfterEach
    public void envReset() {
        env.reset();
    }
    @Test
    public void testIfT() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.TRUE),
                                            new ConstantEvaluable((1)),
                                            new ConstantEvaluable((2)));
        assertEquals((int)(iffer.eval(env)), 1);
    }

    @Test
    public void testIfNonNilString() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable("stringa"),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals((int)(iffer.eval(env)), 1);
    }

    @Test
    public void testIfNonNilInt() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(0),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals((int)(iffer.eval(env)), 1);
    }

    @Test
    public void testIfFalse() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.FALSE),
                                            new ConstantEvaluable((1)),
                                            new ConstantEvaluable((2)));
        assertEquals((int)(iffer.eval(env)), 2);
    }

    @Test
    public void testIfNil() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.NIL),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals((int)(iffer.eval(env)), 1);
    }
}
