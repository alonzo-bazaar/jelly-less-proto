package eval;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.Constants;

public class IfEvaluableTest {
    private Environment env;
    @Before
    public void envSetup() {
        env = new Environment();
    }
    @After
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
