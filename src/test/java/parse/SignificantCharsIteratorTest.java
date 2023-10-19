package parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import parse.SignificantCharsIterator;
import utils.StringCharIterator;

public class SignificantCharsIteratorTest 
{
    private SignificantCharsIterator fromString(String s) {
        return new SignificantCharsIterator(new StringCharIterator(s));
    }
    @Test
    public void baseIgnoraMultiline()
    {
        SignificantCharsIterator sci = fromString("/* ok */a");
        char c = sci.next();
        assertEquals(c, 'a');
    }

    @Test
    public void baseNonIgnoraTutto()
    {
        SignificantCharsIterator sci = fromString("a/* ok */b");
        char c = sci.next();
        assertEquals(c, 'a');
        c = sci.next();
        assertEquals(c, 'b');
        assertFalse(sci.hasNext());
    }

    @Test
    public void inlineDosIgnoresDos() {
        SignificantCharsIterator sci = fromString("// this is mazzo\r\njuuj").emulateDos();
        char c = sci.next();
        assertEquals(c, 'j');
    }

    @Test
    public void inlineDosDoesntIgnoreUnix() {
        SignificantCharsIterator sci = fromString("// o\njk").emulateDos();
        assertFalse(sci.hasNext());
    }

    @Test
    public void inlineUnixIgnoresUnix() {
        SignificantCharsIterator sci = fromString("// o\njk").emulateUnix();
        assertTrue(sci.hasNext());
        char c = sci.next();
        assertEquals(c,'j');
        c = sci.next();
        assertEquals(c,'k');
        assertFalse(sci.hasNext());
    }

    /* una newline dos finisce con una newline unix quindi non è che sia
     * questo gran macello far gestire una newline dos a una newline unix
     * ma abeh, sticazzi ormai
     */
    @Test
    public void testAllaCazzo() {
        SignificantCharsIterator sci = fromString("// o\r\njk").emulateUnix();
        assertTrue(sci.hasNext());
        char c = sci.next();
        assertEquals(c,'j');
        c = sci.next();
        assertEquals(c,'k');
        assertFalse(sci.hasNext());
    }
}
