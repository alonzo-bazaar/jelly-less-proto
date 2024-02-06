package org.jelly.eval;


import org.jelly.eval.evaluable.ConstantEvaluable;
import org.jelly.eval.evaluable.IfForm;
import org.jelly.eval.runtime.Environment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.Assert.assertEquals;

import org.jelly.lang.data.Constants;

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
        IfForm iffer = new IfForm(new ConstantEvaluable(Constants.TRUE),
                                            new ConstantEvaluable((1)),
                                            new ConstantEvaluable((2)));
        assertEquals(1, (int)(iffer.eval(env)));
    }

    @Test
    public void testIfNonNilString() {
        IfForm iffer = new IfForm(new ConstantEvaluable("stringa"),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals(1, (int)(iffer.eval(env)));
    }

    @Test
    public void testIfNonNilInt() {
        IfForm iffer = new IfForm(new ConstantEvaluable(0),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals(1, (int)(iffer.eval(env)));
    }

    @Test
    public void testIfFalse() {
        IfForm iffer = new IfForm(new ConstantEvaluable(Constants.FALSE),
                                            new ConstantEvaluable((1)),
                                            new ConstantEvaluable((2)));
        assertEquals(2, (int)(iffer.eval(env)));
    }

    @Test
    public void testIfNil() {
        IfForm iffer = new IfForm(new ConstantEvaluable(Constants.NIL),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals(1, (int)(iffer.eval(env)));
    }
}
