package org.jelly.lang.javaffi;

// see LabRat class for the semantics behind the LabRat calls

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JavaFFITest {
    @BeforeEach
    public void ensureRepeatable() {
        LabRat.resetStaticData();
    }

    @Test
    public void testCallLabRat() {
        LabRat lr = new LabRat(10);
        assertEquals(20, (int)ForeignMethodCaller.call(lr, "add", new Object[] {10}));;
        assertEquals(5, (int)ForeignMethodCaller.call(lr, "sub", new Object[] {15}));;
    }

    @Test
    public void testCallstaticLabRat() {
        assertEquals(1, (int) ForeignMethodCaller.callStatic(LabRat.class, "upCounter", new Object[]{}));
        assertEquals(2, (int) ForeignMethodCaller.callStatic(LabRat.class, "upCounter", new Object[]{}));
        assertEquals(3, (int) ForeignMethodCaller.callStatic(LabRat.class, "upCounter", new Object[]{}));
    }

    @Test
    public void testStringCall() {
        assertEquals(5, (int) ForeignMethodCaller.call("mamma", "length", new Object[] {}));
        assertEquals("MAMMA", ForeignMethodCaller.call("mamma", "toUpperCase", new Object[] {}));
    }

    @Test
    public void testTryCallBad() {
        LabRat lr = new LabRat(10);
        Object res = ForeignMethodCaller.tryCall(lr, "canThrow", new Object[] {0});
        assertInstanceOf(BadResult.class, res);
        assertEquals("exception", ((BadResult<?,?>)res).getErrorMessage());
    }

    @Test
    public void testTryCallGood() {
        LabRat lr = new LabRat(10);
        Object res = ForeignMethodCaller.tryCall(lr, "canThrow", new Object[] {1});
        assertInstanceOf(GoodResult.class, res);
        assertEquals(10, ((GoodResult<?,?>)res).get());

    }

    @Test
    public void testTryCallStaticBad() {
        Object res = ForeignMethodCaller.tryCallStatic(LabRat.class, "canThrowStatic", new Object[]{0});
        assertInstanceOf(BadResult.class, res);
        assertEquals("exception", ((BadResult<?,?>)res).getErrorMessage());
    }

    @Test
    public void testTryCallStaticGood() {
        Object res = ForeignMethodCaller.tryCallStatic(LabRat.class, "canThrowStatic", new Object[]{1});
        assertInstanceOf(GoodResult.class, res);
        assertEquals(1, ((GoodResult<?,?>)res).get());
    }
}

