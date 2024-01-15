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
        assertEquals(10, lr.get());
    }

    @Test
    public void testLabRatStringConstructor() {
        LabRat lr = (LabRat) ForeignMethodCaller.construct(LabRat.class, new Object[]{"mamma"});
        assertEquals(5, lr.get());
    }

    @Test
    public void testLabRatConstructorMultipleParameters() {
        LabRat lr = (LabRat) ForeignMethodCaller.construct(LabRat.class, new Object[]{10, 20});
        assertEquals(10, lr.get());
        assertEquals(20, LabRat.getCounter());
    }
}
