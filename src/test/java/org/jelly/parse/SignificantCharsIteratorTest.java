package org.jelly.parse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SignificantCharsIteratorTest 
{
    @Test
    public void baseIgnoraMultiline()
    {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("#| ok |#a");
        assertTrue(Character.isWhitespace(sci.next()));
        assertEquals(sci.next(), 'a');
    }

    @Test
    public void baseNonIgnoraTutto()
    {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("a#| ok |#b");
        assertEquals(sci.next(), 'a');
        assertTrue(Character.isWhitespace(sci.next()));
        assertEquals(sci.next(), 'b');
        assertFalse(sci.hasNext());
    }

    @Test
    public void inlineDosIgnoresDos() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("; this is mazzo\r\njuuj").emulateDos();
        assertTrue(sci.hasNext());
        assertTrue(Character.isWhitespace(sci.next()));
        assertEquals(sci.next(), 'j');
        assertEquals(sci.next(), 'u');
        assertEquals(sci.next(), 'u');
        assertEquals(sci.next(), 'j');
        assertFalse(sci.hasNext());
    }

    @Test
    public void inlineDosDoesntIgnoreUnix() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("; o\njk").emulateDos();
        assertTrue(sci.hasNext());
        assertTrue(Character.isWhitespace(sci.next()));
        assertFalse(sci.hasNext());
    }

    @Test
    public void inlineUnixIgnoresUnix() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("; o\njk").emulateUnix();
        assertTrue(sci.hasNext());
        assertTrue(Character.isWhitespace(sci.next()));
        assertEquals(sci.next(),'j');
        assertEquals(sci.next(),'k');
        assertFalse(sci.hasNext());
    }

    @Test
    public void multipleColons() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString(";;; o\njk").emulateUnix();
        assertTrue(sci.hasNext());
        assertTrue(Character.isWhitespace(sci.next()));
        assertEquals(sci.next(),'j');
        assertEquals(sci.next(),'k');
        assertFalse(sci.hasNext());
    }

    /* una newline dos finisce con una newline unix quindi non è che sia
     * questo gran macello far gestire una newline dos a una newline unix
     * ma abeh, ormai
     */
    @Test
    public void inlineUnixRules() {
        SignificantCharsIterator sci = SignificantCharsIterator.fromString("; o\r\njk").emulateUnix();
        assertTrue(sci.hasNext());
        assertTrue(Character.isWhitespace(sci.next()));
        assertEquals(sci.next(),'j');
        assertEquals(sci.next(),'k');
        assertFalse(sci.hasNext());
    }
}
