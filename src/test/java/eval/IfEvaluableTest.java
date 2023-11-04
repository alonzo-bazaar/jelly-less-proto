package eval;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.Assert.assertEquals;

import lang.Constants;

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
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.T),
                                            new ConstantEvaluable((1)),
                                            new ConstantEvaluable((2)));
        assertEquals((int)(iffer.eval(env)), 1);
    }

    @Test
    public void testIfNonNil() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable("cristo"),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals((int)(iffer.eval(env)), 1);
    }

    @Test
    public void testElse() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.NIL),
                                            new ConstantEvaluable(1),
                                            new ConstantEvaluable(2));
        assertEquals((int)(iffer.eval(env)), 2);
    }
}
