package utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import junit.framework.TestResult;

public class PrefixIteratorTest
{
    /* prefix iterator tests */
    @Test
    public void ok() {
        PrefixIterator pi = new PrefixIterator(new StringCharIterator("mamma mia"));
        assertFalse(pi.startsWith("mia"));
        assertTrue(pi.startsWith("mamma"));
        pi = new PrefixIterator(new StringCharIterator("mia mamma"));
        assertTrue(pi.startsWith("mia"));
        assertFalse(pi.startsWith("mamma"));
    }

    @Test
    public void robus() {
        
    }
}
