package org.jelly.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PrefixIteratorTest
{
    /* prefix iterator tests */
    private PrefixIterator fromString(String s) {
        return new PrefixIterator(new StringCharIterator(s));
    }
    @Test
    public void ok() {
        PrefixIterator pi = fromString("mamma mia");
        assertFalse(pi.startsWith("mia"));
        assertTrue(pi.startsWith("mamma"));
        pi = fromString("mia mamma");
        assertTrue(pi.startsWith("mia"));
        assertFalse(pi.startsWith("mamma"));
    }
}
