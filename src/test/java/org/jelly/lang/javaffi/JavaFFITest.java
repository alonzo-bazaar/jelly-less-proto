package org.jelly.lang.javaffi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}

