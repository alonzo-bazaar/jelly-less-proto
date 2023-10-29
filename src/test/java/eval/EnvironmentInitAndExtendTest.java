package eval;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import lang.LispExpression;
import lang.LispValue;
import lang.LispSymbol;
import lang.Constants;

public class EnvironmentInitAndExtendTest {
    private Environment env;
    @Before
    public void initializeAndExtend() throws EnvironmentException {
        env = new Environment();
        env.define(new LispSymbol("nope"), Constants.NIL);
        env.extend();
        env.define(new LispSymbol("yee"), Constants.T);
        env.extend();
        env.define(new LispSymbol("yoo"), new LispValue<Integer>(42));
    }

    @Test
    public void testLookup() {
        LispExpression le = env.lookup(new LispSymbol("nope"));
        assertEquals(le, Constants.NIL);
    }

    @Test
    public void testDefine() throws EnvironmentException {
        env.define(new LispSymbol("waluigi"), new LispValue<Integer>(1));
       
        LispExpression le = env.lookup(new LispSymbol("waluigi"));
        if((le instanceof LispValue lv) && (lv.get() instanceof Integer i)) {
            assertEquals((int)i, 1);
        }
        else
            throw new AssertionError("le not of type LispValue<Integer>");
    }

    @Test
    public void testSet() throws EnvironmentException {
        env.set(new LispSymbol("nope"), new LispValue<Integer>(20));
        LispExpression le = env.lookup(new LispSymbol("nope"));
        if((le instanceof LispValue lv) && (lv.get() instanceof Integer i)) {
            assertEquals((int)i, 20);
        }
        else
            throw new AssertionError("le not of type LispValue<Integer>");
    }

    @After
    public void resetEnv() {
        env.reset();
    }
}
