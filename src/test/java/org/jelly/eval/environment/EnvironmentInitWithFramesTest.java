package org.jelly.eval.environment;

import org.jelly.eval.environment.errors.EnvironmentException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.HashMap;

import org.jelly.lang.data.Symbol;
import org.jelly.lang.data.Constants;

public class EnvironmentInitWithFramesTest {
    private Environment env;
    @BeforeEach
    public void initializeWithFrames() {
        HashMap<Symbol, Object> m0 = new HashMap<>();
        m0.put(new Symbol("nope"), Constants.NIL);
        EnvFrame env0 = new EnvFrame(m0);

        HashMap<Symbol, Object> m1 = new HashMap<>();
        m1.put(new Symbol("yee"), Constants.TRUE);
        EnvFrame env1 = new EnvFrame(m1);

        HashMap<Symbol, Object> m2 = new HashMap<>();
        m2.put(new Symbol("yoo"), 42);
        EnvFrame env2 = new EnvFrame(m2);

        env = new Environment(env2, new Environment(env1, new Environment(env0)));
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
