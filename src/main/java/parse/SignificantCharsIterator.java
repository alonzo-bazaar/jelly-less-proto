package parse;

import java.util.Iterator;

import utils.PrefixIterator;

// public class SignificantCharsIterator implements Iterator<Character>{
public class SignificantCharsIterator extends PrefixIterator{
    /**
     * ignora caratteri inutili per la sintassi
     * si intendono quindi
     * - commenti
     * - spazii bianchi superflui
     */

     /* TODO : questa classe sta diventando un po' grossa,
      * visto che ci sto mettendo TUTTO quello che mi serve fare con un iterator
      * spacca un pochino
      */
    // commenti stile java/c++ per non scazzare troppe abitudini (altrui)
    private static String inlineCommentStart = "//";
    private static String multilineCommentStart = "/*";
    private static String multilineCommentEnd = "*/";

    private static String unixNewline = "\n";
    private static String dosNewline = "\r\n";

    private String newline;
    private boolean lastWasWhitespace = false;
    public SignificantCharsIterator(Iterator<Character> ic) {
        super(ic);
        /* un piccolo tentativo a fare cross platform
         * anche se mi sembra un po' difficile testare se fa da windows
         */
        if (isWindows())
            this.newline = dosNewline;
        else 
            this.newline = unixNewline;

        /* onde evitare che dello spazio bianco iniziale possa essere considerato
         * come importante da inviare
         */
        ignoreWhiteSpace();
    }

    private static boolean isWindows() {
        /* this function does not the fuck belong here
         * copy https://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
         * somewhere */
        return (System.getProperty("os.name").startsWith("Windows"));
    }

    @Override
    public Character next() {
        if(this.lastWasWhitespace) ignoreCommentsAndWhitespace();
        else ignoreComments();
        Character c = super.next();
        lastWasWhitespace = (Character.isWhitespace(c));
        return c;
    }

    @Override
    public boolean hasNext() {
        ignoreInsignificant();
        return super.hasNext();
    }

    /* questo per i test, così sappiamo che almeno fa per entrambi i tipi di sistema */
    public SignificantCharsIterator emulateDos() {
        this.newline = dosNewline;
        return this;
    }

    public SignificantCharsIterator emulateUnix() {
        this.newline = unixNewline;
        return this;
    }

    public void ignoreInsignificant() {
        if(this.lastWasWhitespace) {
            ignoreCommentsAndWhitespace();
        }
        else {
            ignoreComments();
        }
    }

    void ignoreCommentsAndWhitespace() {
        while(startsWithComment() || startsWithWhitespace()) {
            ignoreComments();
            ignoreWhiteSpace();
        }
    }

    boolean startsWithComment() {
        return super.startsWith(inlineCommentStart) ||
            super.startsWith(multilineCommentStart);
    }

    public void ignoreComments() {
        boolean ignored = true;
        while(ignored && super.hasNext()) {
            // loop in caso ci siamo più commenti diversi di fila
            ignored = false;
            if (super.startsWith(inlineCommentStart)) {
                ignorePastPrefix(newline);
                ignored = true;
            } 
            else if (super.startsWith(multilineCommentStart)) {
                ignorePastPrefix(multilineCommentEnd);
                ignored = true;
            }
        }
    }

    public void ignoreUntilPrefix(String prefix) {
        while (super.hasNext() && !super.startsWith(prefix)) {
            super.next();
        }
    }

    public void ignorePastPrefix(String prefix) {
        ignoreUntilPrefix(prefix);
        // but we shall also ignore the prefix
        for (int i = 0; i < prefix.length() && super.hasNext() ; ++i) {
            super.next();
        }
    }

    /* per quanto io sia pienamente consapevole del fatto che un "ignora spazii bianchi"
     * non stia proprio da genio in un un "iteratore che ignora i commenti",
     * ogni altro posto per mettere la seguente funzionalità farebbe cagare al cazzo
     * quindi o faccio un'interfaccia a parte solo per questo, oppure vaffanculo che tanto
     * questa funzionalità non verrà utilizzata da iteratori che non siano già di questa
     * classe
     */
    public void ignoreWhiteSpace() {
        /* e qui si ringrazia la gigantesca minchia di guy steele che fare così alla next(), assumendo che tutti gli spazi
         * siano di un singolo carattere, COMUNQUE non rompe il cazzo con '\r\n'
         * che sarebbe l'unico whitespace NON fatto di un solo carattere
         */
        Character c = null;
        while(super.hasNext() && this.startsWithWhitespace()) {
            c = super.next();
        }
    }

    boolean startsWithWhitespace() {
        String[] whitespacePrefixes = {" ", "\t", dosNewline, unixNewline};
        for(String w:whitespacePrefixes) {
            if(super.startsWith(w)) {
                return true;
            }
        }
        return false;
    }
}
