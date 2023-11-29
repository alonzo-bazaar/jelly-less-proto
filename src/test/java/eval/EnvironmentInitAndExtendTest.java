package eval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import lang.LispSymbol;
import lang.Constants;

public class EnvironmentInitAndExtendTest {
    private Environment env;
    @BeforeEach
    public void initializeAndExtend() throws EnvironmentException {
        env = new Environment();
        env.define(new LispSymbol("nope"), Constants.NIL);
        env.extend();
        env.define(new LispSymbol("yee"), Constants.TRUE);
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

    @AfterEach
    public void resetEnv() {
        env.reset();
    }
}
