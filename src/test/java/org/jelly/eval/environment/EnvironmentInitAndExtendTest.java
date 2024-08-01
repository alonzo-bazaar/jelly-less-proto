package org.jelly.eval.environment;

import org.jelly.eval.environment.errors.EnvironmentException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import org.jelly.lang.data.Symbol;
import org.jelly.lang.data.Constants;

public class EnvironmentInitAndExtendTest {
    private Environment env;
    @BeforeEach
    public void initializeAndExtend() throws EnvironmentException {
        Environment env0 = new Environment();
        env0.define(new Symbol("nope"), Constants.NIL);

        Environment env1 = env0.extend();
        env1.define(new Symbol("yee"), Constants.TRUE);

        Environment env2 = env1.extend();
        env2.define(new Symbol("yoo"), 42);

        env = env2;
    }

    @Test
    public void testLookup() {
        Object le = env.lookup(new Symbol("nope"));
        assertEquals(Constants.NIL, le);
    }

    @Test
    public void testDefine() throws EnvironmentException {
        env.define(new Symbol("waluigi"), 1);
       
        Object o = env.lookup(new Symbol("waluigi"));
        assertEquals(1, (int)o);
    }

    @Test
    public void testSet() throws EnvironmentException {
        env.set(new Symbol("nope"), 20);
        Object o = env.lookup(new Symbol("nope"));
        assertEquals(20, (int)o);
    }

    @AfterEach
    public void resetEnv() {
        env.reset();
    }
}
