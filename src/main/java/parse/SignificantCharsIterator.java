package parse;

import java.util.Iterator;

import utils.PrefixIterator;

// public class SignificantCharsIterator implements Iterator<Character>{
public class SignificantCharsIterator extends PrefixIterator{
    /**
     * ignora caratteri inutili per la sintassi
     * si intendono quindi
     * - commenti
     */
    // commenti stile java/c++ per non scazzare troppe abitudini (altrui)
    private static String inlineCommentStart = "//";
    private static String multilineCommentStart = "/*";
    private static String multilineCommentEnd = "*/";

    private static String unixNewline = "\n";
    private static String dosNewline = "\r\n";

    private String newline;
    public SignificantCharsIterator(Iterator<Character> ic) {
        super(ic);
        if (isWindows())
            this.newline = dosNewline;
        else 
            this.newline = unixNewline;
    }

    private static boolean isWindows() {
        /* TODO : this function does not the fuck belong here
         * copy https://stackoverflow.com/questions/228477
         * somewhere */
        return (System.getProperty("os.name").startsWith("Windows"));
    }

    // questo per i test, così sappiamo che almeno fa per entrambi i tipi di sistema
    public SignificantCharsIterator emulateDos() {
        this.newline = dosNewline;
        return this;
    }

    public SignificantCharsIterator emulateUnix() {
        this.newline = unixNewline;
        return this;
    }

    @Override
    public Character next() {
        return this.noCommentNext();
    }

    @Override
    public boolean hasNext() {
        ignoreComments();
        return super.hasNext();
    }

    Character noCommentNext() {
        this.ignoreComments();
        return super.next();
    }

    public void ignoreCommentsAndWhitespace() {
        // guarantees next character starts a significant subsequence in the stream
        // (goes to beginning of the next token)
        while(this.startsWithWhitespace() || this.startsWithComment()) {
            this.noCommentNext();
        }
    }

    boolean startsWithComment() {
        return super.startsWith(inlineCommentStart) ||
            super.startsWith(multilineCommentStart);
    }

    void ignoreComments() {
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

    boolean startsWithWhitespace() {
        return super.startsWith(newline) ||
            (super.hasNext() && Character.isWhitespace(super.peekCharacter()));
    }

    void ignoreUntilPrefix(String prefix) {
        while (super.hasNext() && !super.startsWith(prefix)) {
            super.next();
        }
    }

    void ignorePastPrefix(String prefix) {
        ignoreUntilPrefix(prefix);
        // but we shall also ignore the prefix
        for (int i = 0; i < prefix.length() && super.hasNext() ; ++i) {
            super.next();
        }
    }
}
