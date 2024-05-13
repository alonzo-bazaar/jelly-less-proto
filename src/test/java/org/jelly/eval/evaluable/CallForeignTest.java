package org.jelly.eval.evaluable;

import org.jelly.eval.procedure.Procedure;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.eval.builtinfuns.Utils;
import org.junit.jupiter.api.Test;

import org.jelly.lang.data.LispSymbol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

public class CallForeignTest extends BaseEvaluableTest {
    private Environment env = new Environment();

    @BeforeEach
    public void refreshEnv() {
        env = new JellyRuntime().buildInitialEnvironment();
        env.define(new LispSymbol("findClass"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("getClass", 1, args);
                Utils.ensureSingleOfType("getClass", 0, String.class, args);
                return Class.forName((String) args.get(0));
            } catch (ClassNotFoundException e) {
                return null;
            }
        });
    }

    @AfterEach
    public void resetEnvironment() {
        env.reset();
    }

    @Test
    public void testClass() {
        try {
            TestRatClass testObj = new TestRatClass();
            env.define(new LispSymbol("testObj"), testObj);
            assertEquals("string", (String)fromString("(call testObj \"testMeth\")").eval(env));
        }
        catch(Exception e) {
            throw new AssertionError("oops", e);
        }
    }

    @Test
    public void testClassWithParams() {
        try {
            TestRatClass testObj = new TestRatClass();
            env.define(new LispSymbol("testObj"), testObj);
            assertEquals("stringugo", (String)fromString("(call testObj \"testMeth\" \"ugo\")").eval(env));
            assertEquals("ugostring10", (String)fromString("(call testObj \"testMeth\" \"ugo\" 10)").eval(env));

            assertEquals(60, (int)fromString("(call testObj \"mixal\" 10 20 30)").eval(env));
        }
        catch(Exception e) {
            throw new AssertionError("oops", e);
        }
    }
    @Test
    public void testCallStringLength() {
        assertEquals(5, (int)fromString("(call \"hello\" \"length\")").eval(env));
    }

    @Test
    public void testCallEmptyStringLength() {
        assertEquals(0, (int)fromString("(call \"\" \"length\")").eval(env));
    }

    @Test
    public void testCallStaticIntegerParse() {
        assertEquals(10, (int)fromString("(callStatic (findClass \"java.lang.Integer\") \"parseInt\" \"10\")").eval(env));
    }

    /* questo non funziona perchè tutti i tipi primitivi in jelly sono
     * sostituiti da tipi boxed ma getMethod per fare la foreign
     * function interface si aspettta un match esastto dei tipi
     * quindi se voglio chiamare una funzoine che utilizza tipi
     * primitivi devo inventarmi qualcos'altro
     */
    @Test
    public void testCallStaticIntegerParseMoreArgs() {
        assertEquals(255, (int)fromString("(callStatic (findClass \"java.lang.Integer\") \"parseInt\" \"ff\" 16)").eval(env));
    }
}

