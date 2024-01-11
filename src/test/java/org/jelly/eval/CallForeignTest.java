package org.jelly.eval;

import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.EvaluableCreator;
import org.jelly.eval.procedure.Procedure;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.runtime.Runtime;
import org.jelly.eval.utils.ArgUtils;
import org.jelly.lang.errors.ParsingException;
import org.junit.jupiter.api.Test;

import org.jelly.lang.LispSymbol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.jelly.parse.ExpressionIterator;

public class CallForeignTest {
    private Environment env = new Environment();

    @BeforeEach
    public void refreshEnv() {
        env = Runtime.buildInitialEnvironment();
        env.define(new LispSymbol("findClass"), (Procedure) args -> {
            try {
                ArgUtils.ensureSizeExactly("getClass", 1, args);
                ArgUtils.ensureSingleOfType("getClass", 0, String.class, args);
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

    private Evaluable fromString(String s) throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString(s);
        Object le = ei.next();
        return EvaluableCreator.fromExpression(le);
    }


    @Test
    public void testClass() {
        try {
            TestClass testObj = new TestClass();
            env.define(new LispSymbol("testObj"), testObj);
            assertEquals((String)fromString("(call testObj \"testMeth\")").eval(env), "string");
        }
        catch(Exception e) {
            throw new AssertionError("fuck you", e);
        }
    }

    @Test
    public void testClassWithParams() {
        try {
            TestClass testObj = new TestClass();
            env.define(new LispSymbol("testObj"), testObj);
            assertEquals((String)fromString("(call testObj \"testMeth\" \"ugo\")").eval(env), "stringugo");
            assertEquals((String)fromString("(call testObj \"testMeth\" \"ugo\" 10)").eval(env), "ugostring10");

            assertEquals((int)fromString("(call testObj \"mixal\" 10 20 30)").eval(env), 60);
        }
        catch(Exception e) {
            throw new AssertionError("fuck you", e);
        }
    }
    @Test
    public void testCallStringLength() {
        assertEquals((int)fromString("(call \"hello\" \"length\")").eval(env), 5);
    }

    @Test
    public void testCallEmptyStringLength() {
        assertEquals((int)fromString("(call \"\" \"length\")").eval(env), 0);
    }

    @Test
    public void testCallStaticIntegerParse() {
        assertEquals((int) fromString("(callStatic (findClass \"java.lang.Integer\") \"parseInt\" \"10\")").eval(env), 10);
    }

    // questo non funziona perchè tutti i tipi primitivi in jelly sono sostituiti da tipi boxed
    // ma getMethod per fare la foreign function interface si aspettta un match esastto dei tipi
    // quindi se voglio chiamare una funzoine che utilizza tipi primitivi devo inventarmi qualcos'altro
    @Test
    public void testCallStaticIntegerParseMoreArgs() {
        assertEquals((int)fromString("(callStatic (findClass \"java.lang.Integer\") \"parseInt\" \"ff\" 16)").eval(env), 255);
    }
}

