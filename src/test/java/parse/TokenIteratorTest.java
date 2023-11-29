package parse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TokenIteratorTest {
    TokenIterator dosFromString(String s) {
        return new TokenIterator(SignificantCharsIterator.fromString(s).emulateDos());
    }

    @Test
    public void getSpecialNoWhite() {
        TokenIterator ti = TokenIterator.fromString("(prova)");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "prova");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void getSpecialWhite() {
        TokenIterator ti = dosFromString("(  kitemmuort  )");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "kitemmuort");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespace() {
        TokenIterator ti = dosFromString("fat       ");
        assertEquals(ti.next(), "fat");
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpace() {
        TokenIterator ti = dosFromString("      fat");
        assertEquals(ti.next(), "fat");
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpaceSpecial() {
        TokenIterator ti = dosFromString("      ::");
        assertEquals(ti.next(), "::");
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespaceSpecial() {
        TokenIterator ti = dosFromString(":::       ");
        assertEquals(ti.next(), ":::");
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespace() {
        TokenIterator ti = dosFromString("   kitemmuort       ");
        assertEquals(ti.next(), "kitemmuort");
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespaceSpecial() {
        TokenIterator ti = dosFromString("   :       ");
        assertEquals(ti.next(), ":");
        assertFalse(ti.hasNext());
    }

    @Test
    public void getsBiggestSpecial() {
        TokenIterator ti = dosFromString(":::::::::::");
        assertEquals(ti.next(), ":::");
        assertEquals(ti.next(), ":::");
        assertEquals(ti.next(), ":::");
        assertEquals(ti.next(), "::");
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentAllParen() {
        TokenIterator ti = dosFromString("(when (that (fat old sun)) in (the sky))");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "when");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "that");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "fat");
        assertEquals(ti.next(), "old");
        assertEquals(ti.next(), "sun");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "in");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "the");
        assertEquals(ti.next(), "sky");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentNoSpacesAllSpecial() {
        TokenIterator ti = dosFromString("(when(that(fat:old::sun)):::in::(the#||#sky))");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "when");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "that");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "fat");
        assertEquals(ti.next(), ":");
        assertEquals(ti.next(), "old");
        assertEquals(ti.next(), "::");
        assertEquals(ti.next(), "sun");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ":::");
        assertEquals(ti.next(), "in");
        assertEquals(ti.next(), "::");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "the");
        assertEquals(ti.next(), "sky");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void whitestHour() {
        TokenIterator ti = dosFromString("mannaggia:  kite\r\n\r\n\r\n\t mmuort\r\n\t");
        assertEquals(ti.next(), "mannaggia");
        assertEquals(ti.next(), ":");
        assertEquals(ti.next(), "kite");
        assertEquals(ti.next(), "mmuort");
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialWhite() { // non il dentifricio
        TokenIterator ti = dosFromString("kono: dio :da");
        assertEquals(ti.next(), "kono");
        assertEquals(ti.next(), ":");
        assertEquals(ti.next(), "dio");
        assertEquals(ti.next(), ":");
        assertEquals(ti.next(), "da");
        assertFalse(ti.hasNext());
    }

    @Test
    public void string() {
        TokenIterator ti = dosFromString("\"stringus\"");
        assertEquals(ti.next(), "\"stringus\"");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringStartingWithWhitespace() {
        TokenIterator ti = dosFromString("\"    stringus\"");
        assertEquals(ti.next(), "\"    stringus\"");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEndingWithWhitespace() {
        TokenIterator ti = dosFromString("\"stringus      \"");
        assertEquals(ti.next(), "\"stringus      \"");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringBeforeTokens() {
        TokenIterator ti = dosFromString("\"something\"in the way  ");
        assertEquals(ti.next(), "\"something\"");
        assertEquals(ti.next(), "in");
        assertEquals(ti.next(), "the");
        assertEquals(ti.next(), "way");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringAfterTokens() {
        TokenIterator ti = dosFromString("çeci est une\"stringa\"");
        assertEquals(ti.next(), "çeci");
        assertEquals(ti.next(), "est");
        assertEquals(ti.next(), "une");
        assertEquals(ti.next(), "\"stringa\"");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleStrings() {
        TokenIterator ti = dosFromString("\"jojo\" \"bizarre\" \"strings\"");
        assertEquals(ti.next(), "\"jojo\"");
        assertEquals(ti.next(), "\"bizarre\"");
        assertEquals(ti.next(), "\"strings\"");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleAttachedStrings() {
        TokenIterator ti = dosFromString("\"jojo\"\"bizarre\"\"strings\"");
        assertEquals(ti.next(), "\"jojo\"");
        assertEquals(ti.next(), "\"bizarre\"");
        assertEquals(ti.next(), "\"strings\"");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringsAndSpecials() {
        TokenIterator ti = dosFromString("(strcat\"kite\"\"mmurt\" \"mannaggia \\\"\" bello bello\"mannaggia\")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "strcat");
        assertEquals(ti.next(), "\"kite\"");
        assertEquals(ti.next(), "\"mmurt\"");
        assertEquals(ti.next(), "\"mannaggia \"\"");
        assertEquals(ti.next(), "bello");
        assertEquals(ti.next(), "bello");
        assertEquals(ti.next(), "\"mannaggia\"");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void emptyString() {
        TokenIterator ti = dosFromString("\"\"");
        assertEquals(ti.next(), "\"\"");
        assertFalse(ti.hasNext());
    }
    
    @Test
    public void multipleEmptyStrings() {
        TokenIterator ti = dosFromString("\"\"\"\"\"\"");
        assertEquals(ti.next(), "\"\"");
        assertEquals(ti.next(), "\"\"");
        assertEquals(ti.next(), "\"\"");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupid() {
        TokenIterator ti = dosFromString("((((((((((((((((((((");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupidWhitespace() {
        TokenIterator ti = dosFromString(" ((    ((((((((((((((((((  ");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "(");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupider() {
        TokenIterator ti = dosFromString("()()()()()()()()()()");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupiderWhitespace() {
        TokenIterator ti = dosFromString("() ( )()()()(  )()()()() ");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlinedMultlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        TokenIterator ti = dosFromString("ti voglio #| bene |# bastonare");
        assertEquals(ti.next(), "ti");
        assertEquals(ti.next(), "voglio");
        assertEquals(ti.next(), "bastonare");
    }

    @Test
    public void ignoresMultlineComments() {
        TokenIterator ti = dosFromString("ti voglio #| bene\r\n |# bastonare");
        assertEquals(ti.next(), "ti");
        assertEquals(ti.next(), "voglio");
        assertEquals(ti.next(), "bastonare");
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        TokenIterator ti = dosFromString("ti voglio ;; bastonare \r\n benissimo; muori  \r\n <3");
        assertEquals(ti.next(), "ti");
        assertEquals(ti.next(), "voglio");
        assertEquals(ti.next(), "benissimo");
        assertEquals(ti.next(), "<3");
        assertFalse(ti.hasNext());
    }
}
