package utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * ricorda
 * lo sai come è fatto da dentro, massacralo
 */
public class PrefixIteratorTest
{
    /* prefix iterator tests */
    @Test
    public void ok() {
        assertTrue(true);
    }

    /* circular char buffer tests */
    /* prima senza utilizzare l'iterator sovrastante */

    @Test
    public void bufferPushPoppingSize() {
        CircularCharBuffer ccb = new CircularCharBuffer(null);
        assertEquals(ccb.size(), 0);
        ccb.push('a'); assertEquals(ccb.size(), 1);
        ccb.push('a'); assertEquals(ccb.size(), 2);
        ccb.push('a'); assertEquals(ccb.size(), 3);
        ccb.push('a'); assertEquals(ccb.size(), 4);
        ccb.pop(); assertEquals(ccb.size(), 3);
        ccb.pop(); assertEquals(ccb.size(), 2);
        ccb.push('a'); assertEquals(ccb.size(), 3);
        ccb.push('a'); assertEquals(ccb.size(), 4);
        ccb.pop(); assertEquals(ccb.size(), 3);
        ccb.pop(); assertEquals(ccb.size(), 2);
        ccb.pop(); assertEquals(ccb.size(), 1);
        ccb.pop(); assertEquals(ccb.size(), 0);
        ccb.push('a'); assertEquals(ccb.size(), 1);
        ccb.pop(); assertEquals(ccb.size(), 0);
    }
    @Test
    public void bufferPushingPrefix() {
        CircularCharBuffer ccb = new CircularCharBuffer(null);
        assertTrue(ccb.isPrefix(""));
        assertFalse(ccb.isPrefix("a"));
        assertFalse(ccb.isPrefix("ab"));
        assertFalse(ccb.isPrefix("abc"));
        assertEquals(ccb.indexAfter(0), 0); /* unico index valido con buffer a 1 */

        ccb.push('a');
        assertTrue(ccb.isPrefix(""));
        assertTrue(ccb.isPrefix("a"));
        assertFalse(ccb.isPrefix("ab"));
        assertFalse(ccb.isPrefix("abc"));

        ccb.push('b');
        assertTrue(ccb.isPrefix(""));
        assertTrue(ccb.isPrefix("a"));
        assertTrue(ccb.isPrefix("ab"));
        assertFalse(ccb.isPrefix("abc"));

        ccb.push('c');
        assertTrue(ccb.isPrefix(""));
        assertTrue(ccb.isPrefix("a"));
        assertTrue(ccb.isPrefix("ab"));
        assertTrue(ccb.isPrefix("abc"));
    }

    public void bufferPushPoppingPrefix() {
        //CircularCharBuffer ccb = new CircularCharBuffer(null);

    }

    public void bufferExhaustErrors() {
        //CircularCharBuffer ccb = new CircularCharBuffer(null);
        CircularCharBuffer ccb = new CircularCharBuffer(null);
        char c;

        ccb.push('a');
        assertTrue(ccb.isPrefix(""));
        assertTrue(ccb.isPrefix("a"));

        c = ccb.pop();
        assertTrue(ccb.isPrefix(""));
        assertFalse(ccb.isPrefix("a"));
        assertEquals(c, 'a');

        ccb.push('a');
        assertTrue(ccb.isPrefix(""));
        assertTrue(ccb.isPrefix("a"));

        ccb.push('b');
        assertTrue(ccb.isPrefix(""));
        assertTrue(ccb.isPrefix("a"));
        assertTrue(ccb.isPrefix("ab"));

        c = ccb.pop();
        assertTrue(ccb.isPrefix(""));
        assertFalse(ccb.isPrefix("a"));
        assertTrue(ccb.isPrefix("b"));
        assertEquals(c, 'b');
    }

    /* con l'iterator sovrastante */
}
