package eval;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.LinkedList;
import java.util.HashMap;

import lang.LispExpression;
import lang.LispValue;
import lang.LispSymbol;
import lang.Constants;

public class EnvironmentInitWithFramesTest {
    private Environment env;
    @Before
    public void initializeWithFrames() {
        LinkedList<EnvFrame> envLst = new LinkedList<>();

        HashMap<String, LispExpression> m0 = new HashMap<>();
        m0.put("nope", Constants.NIL);
        EnvFrame env0 = new EnvFrame(m0);

        HashMap<String, LispExpression> m1 = new HashMap<>();
        m1.put("yee", Constants.T);
        EnvFrame env1 = new EnvFrame(m1);

        HashMap<String, LispExpression> m2 = new HashMap<>();
        m2.put("yoo", new LispValue<Integer>(42));
        EnvFrame env2 = new EnvFrame(m2);

        envLst.addLast(env0);
        envLst.addLast(env1);
        envLst.addLast(env2);
        env = new Environment(envLst);
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
    }

    @Test
    public void testSet() throws EnvironmentException {
        env.set(new LispSymbol("nope"), new LispValue<Integer>(20));
        LispExpression le = env.lookup(new LispSymbol("nope"));
        if((le instanceof LispValue lv) && (lv.get() instanceof Integer i)) {
            assertEquals((int)i, 20);
        }
    }

    @After
    public void resetEnv() {
        env.reset();
    }
}
