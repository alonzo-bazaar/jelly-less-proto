package eval;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import lang.Constants;
import lang.LispExpression;
import lang.LispValue;
import lang.LispSymbol;

public class IfEvaluableTest {
    @Test
    public void testIfT() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.T),
                                            new ConstantEvaluable(new LispValue<Integer>(1)),
                                            new ConstantEvaluable(new LispValue<Integer>(2)));
        if (iffer.eval(new Environment()) instanceof LispValue lv && lv.get() instanceof Integer i) {
            assertEquals((int)i, 1);
        }
        else
            throw new AssertionError("not of expected type");
    }

    @Test
    public void testIfNonNil() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(new LispValue<String>("cristo")),
                                            new ConstantEvaluable(new LispValue<Integer>(1)),
                                            new ConstantEvaluable(new LispValue<Integer>(2)));
        if (iffer.eval(new Environment()) instanceof LispValue lv && lv.get() instanceof Integer i) {
            assertEquals((int)i, 1);
        }
        else
            throw new AssertionError("not of expected type");
    }

    @Test
    public void testElse() {
        IfEvaluable iffer = new IfEvaluable(new ConstantEvaluable(Constants.NIL),
                                            new ConstantEvaluable(new LispValue<Integer>(1)),
                                            new ConstantEvaluable(new LispValue<Integer>(2)));
        if (iffer.eval(new Environment()) instanceof LispValue lv && lv.get() instanceof Integer i) {
            assertEquals((int)i, 2);
        }
        else
            throw new AssertionError("not of expected type");
    }
}
