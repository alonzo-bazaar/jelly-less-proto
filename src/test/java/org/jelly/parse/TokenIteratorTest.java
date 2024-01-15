package org.jelly.parse;

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
        assertEquals("(", ti.next());
        assertEquals("prova", ti.next());
        assertEquals(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void getSpecialWhite() {
        TokenIterator ti = dosFromString("(  kitemmuort  )");
        assertEquals("(", ti.next());
        assertEquals("kitemmuort", ti.next());
        assertEquals(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespace() {
        TokenIterator ti = dosFromString("fat       ");
        assertEquals("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpace() {
        TokenIterator ti = dosFromString("      fat");
        assertEquals("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpaceSpecial() {
        TokenIterator ti = dosFromString("      ::");
        assertEquals("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespaceSpecial() {
        TokenIterator ti = dosFromString(":::       ");
        assertEquals(":::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespace() {
        TokenIterator ti = dosFromString("   kitemmuort       ");
        assertEquals("kitemmuort", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespaceSpecial() {
        TokenIterator ti = dosFromString("   :       ");
        assertEquals(":", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void getsBiggestSpecial() {
        TokenIterator ti = dosFromString(":::::::::::");
        assertEquals(":::", ti.next());
        assertEquals(":::", ti.next());
        assertEquals(":::", ti.next());
        assertEquals("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentAllParen() {
        TokenIterator ti = dosFromString("(when (that (fat old sun)) in (the sky))");
        assertEquals("(", ti.next());
        assertEquals("when", ti.next());
        assertEquals("(", ti.next());
        assertEquals("that", ti.next());
        assertEquals("(", ti.next());
        assertEquals("fat", ti.next());
        assertEquals("old", ti.next());
        assertEquals("sun", ti.next());
        assertEquals(")", ti.next());
        assertEquals(")", ti.next());
        assertEquals("in", ti.next());
        assertEquals("(", ti.next());
        assertEquals("the", ti.next());
        assertEquals("sky", ti.next());
        assertEquals(")", ti.next());
        assertEquals(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentNoSpacesAllSpecial() {
        TokenIterator ti = dosFromString("(when(that(fat:old::sun)):::in::(the#||#sky))");
        assertEquals("(", ti.next());
        assertEquals("when", ti.next());
        assertEquals("(", ti.next());
        assertEquals("that", ti.next());
        assertEquals("(", ti.next());
        assertEquals("fat", ti.next());
        assertEquals(":", ti.next());
        assertEquals("old", ti.next());
        assertEquals("::", ti.next());
        assertEquals("sun", ti.next());
        assertEquals(")", ti.next());
        assertEquals(")", ti.next());
        assertEquals(":::", ti.next());
        assertEquals("in", ti.next());
        assertEquals("::", ti.next());
        assertEquals("(", ti.next());
        assertEquals("the", ti.next());
        assertEquals("sky", ti.next());
        assertEquals(")", ti.next());
        assertEquals(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void whitestHour() {
        TokenIterator ti = dosFromString("mannaggia:  kite\r\n\r\n\r\n\t mmuort\r\n\t");
        assertEquals("mannaggia", ti.next());
        assertEquals(":", ti.next());
        assertEquals("kite", ti.next());
        assertEquals("mmuort", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialWhite() { // non il dentifricio
        TokenIterator ti = dosFromString("kono: dio :da");
        assertEquals("kono", ti.next());
        assertEquals(":", ti.next());
        assertEquals("dio", ti.next());
        assertEquals(":", ti.next());
        assertEquals("da", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void string() {
        TokenIterator ti = dosFromString("\"stringus\"");
        assertEquals("\"stringus\"", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringStartingWithWhitespace() {
        TokenIterator ti = dosFromString("\"    stringus\"");
        assertEquals("\"    stringus\"", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEndingWithWhitespace() {
        TokenIterator ti = dosFromString("\"stringus      \"");
        assertEquals("\"stringus      \"", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringBeforeTokens() {
        TokenIterator ti = dosFromString("\"something\"in the way  ");
        assertEquals("\"something\"", ti.next());
        assertEquals("in", ti.next());
        assertEquals("the", ti.next());
        assertEquals("way", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringAfterTokens() {
        TokenIterator ti = dosFromString("çeci est une\"stringa\"");
        assertEquals("çeci", ti.next());
        assertEquals("est", ti.next());
        assertEquals("une", ti.next());
        assertEquals("\"stringa\"", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleStrings() {
        TokenIterator ti = dosFromString("\"jojo\" \"bizarre\" \"strings\"");
        assertEquals("\"jojo\"", ti.next());
        assertEquals("\"bizarre\"", ti.next());
        assertEquals("\"strings\"", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleAttachedStrings() {
        TokenIterator ti = dosFromString("\"jojo\"\"bizarre\"\"strings\"");
        assertEquals("\"jojo\"", ti.next());
        assertEquals("\"bizarre\"", ti.next());
        assertEquals("\"strings\"", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringsAndSpecials() {
        TokenIterator ti = dosFromString("(strcat\"kite\"\"mmurt\" \"mannaggia \\\"\" bello bello\"mannaggia\")");
        assertEquals("(", ti.next());
        assertEquals("strcat", ti.next());
        assertEquals("\"kite\"", ti.next());
        assertEquals("\"mmurt\"", ti.next());
        assertEquals("\"mannaggia \"\"", ti.next());
        assertEquals("bello", ti.next());
        assertEquals("bello", ti.next());
        assertEquals("\"mannaggia\"", ti.next());
        assertEquals(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void emptyString() {
        TokenIterator ti = dosFromString("\"\"");
        assertEquals("\"\"", ti.next());
        assertFalse(ti.hasNext());
    }
    
    @Test
    public void multipleEmptyStrings() {
        TokenIterator ti = dosFromString("\"\"\"\"\"\"");
        assertEquals("\"\"", ti.next());
        assertEquals("\"\"", ti.next());
        assertEquals("\"\"", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupid() {
        TokenIterator ti = dosFromString("((((((((((((((((((((");
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupidWhitespace() {
        TokenIterator ti = dosFromString(" ((    ((((((((((((((((((  ");
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertEquals("(", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupider() {
        TokenIterator ti = dosFromString("()()()()()()()()()()");
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupiderWhitespace() {
        TokenIterator ti = dosFromString("() ( )()()()(  )()()()() ");
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertEquals("(", ti.next());
        assertEquals(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlinedMultlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        TokenIterator ti = dosFromString("ti voglio #| bene |# bastonare");
        assertEquals("ti", ti.next());
        assertEquals("voglio", ti.next());
        assertEquals("bastonare", ti.next());
    }

    @Test
    public void ignoresMultlineComments() {
        TokenIterator ti = dosFromString("ti voglio #| bene\r\n |# bastonare");
        assertEquals("ti", ti.next());
        assertEquals("voglio", ti.next());
        assertEquals("bastonare", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        TokenIterator ti = dosFromString("ti voglio ;; bastonare \r\n benissimo; muori  \r\n <3");
        assertEquals("ti", ti.next());
        assertEquals("voglio", ti.next());
        assertEquals("benissimo", ti.next());
        assertEquals("<3", ti.next());
        assertFalse(ti.hasNext());
    }
}
