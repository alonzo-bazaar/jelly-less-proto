package parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import parse.SignificantCharsIterator;
import utils.StringCharIterator;

public class SignificantCharsIteratorTest 
{
    /**
     * Rigorous Test :-)
     */
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
    }

    @Test
    public void inlineDosIgnoresDos() {
        SignificantCharsIterator sci = fromString("// this is mazzo\r\njuuj")
            .emulateDos();
        char c = sci.next();
        System.out.println("MAMMA <" + c + "> MIA");
        assertEquals(c, 'j');
    }

    @Test
    public void inlineDosDoesntIgnoreUnix() {
        SignificantCharsIterator sci = fromString("// o\njj").emulateDos();
        assertFalse(sci.hasNext());
    }
}
