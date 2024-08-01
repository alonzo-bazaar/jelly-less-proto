package org.jelly.eval.evaluable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CallForeignTest extends BaseEvaluableTest {
    @Test
    public void testClass() {
        try {
            TestRatClass testObj = new TestRatClass();
            define("testObj", testObj);
            assertEquals("string", (String)eval("(call testObj \"testMeth\")"));
        }
        catch(Exception e) {
            throw new AssertionError("oops", e);
        }
    }

    @Test
    public void testClassWithParams() {
        try {
            TestRatClass testObj = new TestRatClass();
            define("testObj", testObj);
            assertEquals("stringugo", (String)eval("(call testObj \"testMeth\" \"ugo\")"));
            assertEquals("ugostring10", (String)eval("(call testObj \"testMeth\" \"ugo\" 10)"));

            assertEquals(60, (int)eval("(call testObj \"mixal\" 10 20 30)"));
        }
        catch(Exception e) {
            throw new AssertionError("oops", e);
        }
    }
    @Test
    public void testCallStringLength() {
        assertEquals(5, (int)eval("(call \"hello\" \"length\")"));
    }

    @Test
    public void testCallEmptyStringLength() {
        assertEquals(0, (int)eval("(call \"\" \"length\")"));
    }

    @Test
    public void testCallStaticIntegerParse() {
        assertEquals(10, (int)eval("(callStatic (findClass \"java.lang.Integer\") \"parseInt\" \"10\")"));
    }

    /* questo non funziona perchè tutti i tipi primitivi in jelly sono
     * sostituiti da tipi boxed ma getMethod per fare la foreign
     * function interface si aspettta un match esastto dei tipi
     * quindi se voglio chiamare una funzoine che utilizza tipi
     * primitivi devo inventarmi qualcos'altro
     */
    @Test
    public void testCallStaticIntegerParseMoreArgs() {
        assertEquals(255, (int)eval("(callStatic (findClass \"java.lang.Integer\") \"parseInt\" \"ff\" 16)"));
    }
}