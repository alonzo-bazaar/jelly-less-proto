package org.jelly.lang.javaffi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstructorFFITest {
    @BeforeEach
    public void ensureRepeatable() {
        LabRat.resetStaticData();
    }

    @Test
    public void testLabRatIntConstructor() {
        LabRat lr = (LabRat) ForeignMethodCaller.construct(LabRat.class, new Object[]{10});
        assertEquals(lr.get(), 10);
    }

    @Test
    public void testLabRatStringConstructor() {
        LabRat lr = (LabRat) ForeignMethodCaller.construct(LabRat.class, new Object[]{"mamma"});
        assertEquals(lr.get(), 5);
    }

    @Test
    public void testLabRatConstructorMultipleParameters() {
        LabRat lr = (LabRat) ForeignMethodCaller.construct(LabRat.class, new Object[]{10, 20});
        assertEquals(lr.get(), 10);
        assertEquals(LabRat.getCounter(), 20);
    }
}
