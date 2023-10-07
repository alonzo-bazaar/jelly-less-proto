package utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

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
