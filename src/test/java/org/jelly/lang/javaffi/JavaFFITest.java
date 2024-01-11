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
        assertEquals((int)ForeignMethodCaller.call(lr, "add", new Object[] {10}), 20);;
        assertEquals((int)ForeignMethodCaller.call(lr, "sub", new Object[] {15}), 5);;
    }

    @Test
    public void testCallstaticLabRat() {
        assertEquals((int) ForeignMethodCaller.callStatic(LabRat.class, "upCounter", new Object[]{}), 1);
        assertEquals((int) ForeignMethodCaller.callStatic(LabRat.class, "upCounter", new Object[]{}), 2);
        assertEquals((int) ForeignMethodCaller.callStatic(LabRat.class, "upCounter", new Object[]{}), 3);
    }

    @Test
    public void testStringCall() {
        assertEquals((int) ForeignMethodCaller.call("mamma", "length", new Object[] {}), 5);
        assertEquals(ForeignMethodCaller.call("mamma", "toUpperCase", new Object[] {}), "MAMMA");
    }
}

