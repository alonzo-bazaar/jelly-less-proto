package eval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.LinkedList;
import java.util.HashMap;

import lang.LispSymbol;
import lang.Constants;

public class EnvironmentInitWithFramesTest {
    private Environment env;
    @BeforeEach
    public void initializeWithFrames() {
        LinkedList<EnvFrame> envLst = new LinkedList<>();

        HashMap<String, Object> m0 = new HashMap<>();
        m0.put("nope", Constants.NIL);
        EnvFrame env0 = new EnvFrame(m0);

        HashMap<String, Object> m1 = new HashMap<>();
        m1.put("yee", Constants.T);
        EnvFrame env1 = new EnvFrame(m1);

        HashMap<String, Object> m2 = new HashMap<>();
        m2.put("yoo", 42);
        EnvFrame env2 = new EnvFrame(m2);

        envLst.addLast(env0);
        envLst.addLast(env1);
        envLst.addLast(env2);
        env = new Environment(envLst);
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
