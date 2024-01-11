package org.jelly.eval;

import org.jelly.eval.runtime.Environment;
import org.jelly.eval.runtime.errors.EnvironmentException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.jelly.lang.LispSymbol;
import org.jelly.lang.Constants;

public class EnvironmentInitAndExtendTest {
    private Environment env;
    @BeforeEach
    public void initializeAndExtend() throws EnvironmentException {
        Environment env0 = new Environment();
        env0.define(new LispSymbol("nope"), Constants.NIL);

        Environment env1 = env0.extend();
        env1.define(new LispSymbol("yee"), Constants.TRUE);

        Environment env2 = env1.extend();
        env2.define(new LispSymbol("yoo"), 42);

        env = env2;
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
