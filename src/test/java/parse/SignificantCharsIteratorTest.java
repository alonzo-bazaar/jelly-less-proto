package parse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import parse.SignificantCharsIterator;
import utils.StringCharIterator;

public class SignificantCharsIteratorTest 
{
    @Test
    public void baseIgnoraMultiline()
    {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("/* ok */a");
        char c = sci.next();
        assertEquals(c, 'a');
    }

    @Test
    public void baseNonIgnoraTutto()
    {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("a/* ok */b");
        char c = sci.next();
        assertEquals(c, 'a');
        c = sci.next();
        assertEquals(c, 'b');
        assertFalse(sci.hasNext());
    }

    @Test
    public void inlineDosIgnoresDos() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("// this is mazzo\r\njuuj").emulateDos();
        char c = sci.next();
        assertEquals(c, 'j');
    }

    @Test
    public void inlineDosDoesntIgnoreUnix() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("// o\njk").emulateDos();
        assertFalse(sci.hasNext());
    }

    @Test
    public void inlineUnixIgnoresUnix() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("// o\njk").emulateUnix();
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
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("// o\r\njk").emulateUnix();
        assertTrue(sci.hasNext());
        char c = sci.next();
        assertEquals(c,'j');
        c = sci.next();
        assertEquals(c,'k');
        assertFalse(sci.hasNext());
    }
}
