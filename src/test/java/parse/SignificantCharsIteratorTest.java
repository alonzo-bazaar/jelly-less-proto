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

    @Test
    public void noRepeatedWhitespace() {
        SignificantCharsIterator sci = fromString("       way     land");
        char c = sci.next(); assertEquals(c, 'w');
        c = sci.next(); assertEquals(c, 'a');
        c = sci.next(); assertEquals(c, 'y');
        c = sci.next(); assertEquals(c, ' ');
        c = sci.next(); assertEquals(c, 'l');
        c = sci.next(); assertEquals(c, 'a');
        c = sci.next(); assertEquals(c, 'n');
        c = sci.next(); assertEquals(c, 'd');
        assertFalse(sci.hasNext());
    }

    @Test
    public void testIgnoreWhitespace() {
        SignificantCharsIterator sci = fromString("tua     madre");
        char c = sci.next(); assertEquals(c, 't');
        c = sci.next(); assertEquals(c, 'u');
        c = sci.next(); assertEquals(c, 'a');
        sci.ignoreWhiteSpace();
        c = sci.next(); assertEquals(c, 'm');
        c = sci.next(); assertEquals(c, 'a');
        c = sci.next(); assertEquals(c, 'd');
        c = sci.next(); assertEquals(c, 'r');
        c = sci.next(); assertEquals(c, 'e');
        assertFalse(sci.hasNext());
    }
    @Test
    public void ignoreWhitespaceAndComments() {
        SignificantCharsIterator sci = fromString("tua  /* non faccciamo commenti */   madre");
        char c = sci.next(); assertEquals(c, 't');
        c = sci.next(); assertEquals(c, 'u');
        c = sci.next(); assertEquals(c, 'a');
        c = sci.next(); assertEquals(c, ' ');
        c = sci.next(); assertEquals(c, 'm');
        c = sci.next(); assertEquals(c, 'a');
        c = sci.next(); assertEquals(c, 'd');
        c = sci.next(); assertEquals(c, 'r');
        c = sci.next(); assertEquals(c, 'e');
        assertFalse(sci.hasNext());
    }

    @Test
    public void ignoreWhitespaceAndMultilineCommentWithNewlines() {
        SignificantCharsIterator sci = fromString("tua  \r\n /* non \r\n facciamo commenti */\t   madre").emulateDos();
        char c = sci.next(); assertEquals(c, 't');
        c = sci.next(); assertEquals(c, 'u');
        c = sci.next(); assertEquals(c, 'a');
        c = sci.next(); assertEquals(c, ' ');
        c = sci.next(); assertEquals(c, 'm');
        c = sci.next(); assertEquals(c, 'a');
        c = sci.next(); assertEquals(c, 'd');
        c = sci.next(); assertEquals(c, 'r');
        c = sci.next(); assertEquals(c, 'e');
        assertFalse(sci.hasNext());
    }

    @Test
    public void whitespaceAndInlineComments() {
        SignificantCharsIterator sci = fromString("elenor \t// lonely motherfucking \r\n rigby").emulateDos();
        char c = sci.next(); assertEquals(c, 'e');
        c = sci.next(); assertEquals(c, 'l');
        c = sci.next(); assertEquals(c, 'e');
        c = sci.next(); assertEquals(c, 'n');
        c = sci.next(); assertEquals(c, 'o');
        c = sci.next(); assertEquals(c, 'r');
        c = sci.next(); assertEquals(c, ' ');
        c = sci.next(); assertEquals(c, 'r');
        c = sci.next(); assertEquals(c, 'i');
        c = sci.next(); assertEquals(c, 'g');
        c = sci.next(); assertEquals(c, 'b');
        c = sci.next(); assertEquals(c, 'y');
        assertFalse(sci.hasNext());
    }
}
