package eval;

import eval.runtime.EnvFrame;
import eval.runtime.Environment;
import eval.runtime.errors.EnvironmentException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.HashMap;

import lang.LispSymbol;
import lang.Constants;

public class EnvironmentInitWithFramesTest {
    private Environment env;
    @BeforeEach
    public void initializeWithFrames() {
        HashMap<LispSymbol, Object> m0 = new HashMap<>();
        m0.put(new LispSymbol("nope"), Constants.NIL);
        EnvFrame env0 = new EnvFrame(m0);

        HashMap<LispSymbol, Object> m1 = new HashMap<>();
        m1.put(new LispSymbol("yee"), Constants.TRUE);
        EnvFrame env1 = new EnvFrame(m1);

        HashMap<LispSymbol, Object> m2 = new HashMap<>();
        m2.put(new LispSymbol("yoo"), 42);
        EnvFrame env2 = new EnvFrame(m2);

        env = new Environment(env2, new Environment(env1, new Environment(env0)));
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
