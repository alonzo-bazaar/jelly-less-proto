package parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import utils.StringCharIterator;

public class TokenIteratorTest {
    TokenIterator fromString(String str) {
        return new TokenIterator(new SignificantCharsIterator(new StringCharIterator(str))
                                 .emulateDos()); /* aboh per quando devo testare il newline */
    }

    @Test
    public void getSpecialNoWhite() {
        TokenIterator ti = fromString("(kitemmuort)");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "kitemmuort");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void getSpecialWhite() {
        TokenIterator ti = fromString("(  kitemmuort  )");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "kitemmuort");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespace() {
        TokenIterator ti = fromString("fat       ");
        assertEquals(ti.next(), "fat");
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpace() {
        TokenIterator ti = fromString("      fat");
        assertEquals(ti.next(), "fat");
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpaceSpecial() {
        TokenIterator ti = fromString("      ;;");
        assertEquals(ti.next(), ";;");
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespaceSpecial() {
        TokenIterator ti = fromString(";;;       ");
        assertEquals(ti.next(), ";;;");
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespace() {
        TokenIterator ti = fromString("   kitemmuort       ");
        assertEquals(ti.next(), "kitemmuort");
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespaceSpecial() {
        TokenIterator ti = fromString("   ;       ");
        assertEquals(ti.next(), ";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void getsBiggestSpecial() {
        TokenIterator ti = fromString(";;;;;;;;;;;");
        assertEquals(ti.next(), ";;;");
        assertEquals(ti.next(), ";;;");
        assertEquals(ti.next(), ";;;");
        assertEquals(ti.next(), ";;");
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentAllParen() {
        TokenIterator ti = fromString("(when (that (fat old sun)) in (the sky))");
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
        TokenIterator ti = fromString("(when(that(fat;old;;sun));;;in#\\(the#||#sky))");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "when");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "that");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "fat");
        assertEquals(ti.next(), ";");
        assertEquals(ti.next(), "old");
        assertEquals(ti.next(), ";;");
        assertEquals(ti.next(), "sun");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ";;;");
        assertEquals(ti.next(), "in");
        assertEquals(ti.next(), "#\\");
        assertEquals(ti.next(), "(");
        assertEquals(ti.next(), "the");
        assertEquals(ti.next(), "#|");
        assertEquals(ti.next(), "|#");
        assertEquals(ti.next(), "sky");
        assertEquals(ti.next(), ")");
        assertEquals(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void whitestHour() {
        TokenIterator ti = fromString("mannaggia;  kite\r\n\r\n\r\n\t mmuort\r\n\t");
        assertEquals(ti.next(), "mannaggia");
        assertEquals(ti.next(), ";");
        assertEquals(ti.next(), "kite");
        assertEquals(ti.next(), "mmuort");
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialWhite() { // non il dentifricio
        TokenIterator ti = fromString("kono; dio ;da");
        assertEquals(ti.next(), "kono");
        assertEquals(ti.next(), ";");
        assertEquals(ti.next(), "dio");
        assertEquals(ti.next(), ";");
        assertEquals(ti.next(), "da");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupid() {
        TokenIterator ti = fromString("((((((((((((((((((((");
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
        TokenIterator ti = fromString(" ((    ((((((((((((((((((  ");
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
        TokenIterator ti = fromString("()()()()()()()()()()");
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
        TokenIterator ti = fromString("() ( )()()()(  )()()()() ");
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
        TokenIterator ti = fromString("ti voglio /* bene */ bastonare");
        assertEquals(ti.next(), "ti");
        assertEquals(ti.next(), "voglio");
        assertEquals(ti.next(), "bastonare");
    }

    @Test
    public void ignoresMultlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        /* il problema erano gli spazi dopo la fine del commento
         * visto che si faceva ignoreWhite l'iterator non capiva di aver
         * "inviato" degli spazi bianchi, quindi non settava lastWasWhite
         * ciò facendo, alla fine inviava lo spazio perchè pensava di doverlo fare
         * visto che era ancora settato per pensare di doverlo fare
         * TODO rivedi la logica interna del lastWasWhite per indicare più un
         * "l'ultima streak di caratteri erano whitespace"
         * quindi magari anche un "ho ignorato blocco non vuoto di spazii,
         * c'erano degli spazi nella stream, fammelo segnare"
         */
        TokenIterator ti = fromString("ti voglio /* bene\r\nkitemmuort */ bastonare");
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
        TokenIterator ti = fromString("ti voglio // bastonare \r\n benissimo// muori  \r\n <3");
        assertEquals(ti.next(), "ti");
        assertEquals(ti.next(), "voglio");
        assertEquals(ti.next(), "benissimo");
        assertEquals(ti.next(), "<3");
        assertFalse(ti.hasNext());
    }
}
