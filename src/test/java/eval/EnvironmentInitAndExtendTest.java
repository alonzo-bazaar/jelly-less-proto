package eval;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

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
        env.define(new LispSymbol("yoo"), 42);
    }

    @Test
    public void testLookup() {
        Object le = env.lookup(new LispSymbol("nope"));
        assertEquals(le, Constants.NIL);
    }

    @Test
    public void testDefine() throws EnvironmentException {
        env.define(new LispSymbol("waluigi"), 1);
       
        Object o = env.lookup(new LispSymbol("waluigi"));
            assertEquals((int)o, 1);
    }

    @Test
    public void testSet() throws EnvironmentException {
        env.set(new LispSymbol("nope"), 20);
        Object o = env.lookup(new LispSymbol("nope"));
            assertEquals((int)o, 20);
    }

    @After
    public void resetEnv() {
        env.reset();
    }
}
