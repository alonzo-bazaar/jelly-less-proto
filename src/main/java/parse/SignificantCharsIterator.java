package parse;

import java.util.Iterator;

import utils.OsUtils;
import utils.PrefixIterator;
import utils.StringCharIterator;

// public class SignificantCharsIterator implements Iterator<Character>{
public class SignificantCharsIterator extends PrefixIterator{
    /**
     * ignora i commenti nel codice
     * vengono inseriti spazi virtuali al posto dei commenti per preservare
     * il fatto che i commenti possano effettivamente separare token
     * non ha la responsabilità di ignorare spazi superflui
     * quella è gestita meglio al livello del TokenIterator
     */
    private static final String inlineCommentStart = ";";
    private static final String multilineCommentStart = "#|";
    private static final String multilineCommentEnd = "|#";

    private static final String unixNewline = "\n";
    private static final String dosNewline = "\r\n";

    private String newline = OsUtils.isWindows()?dosNewline:unixNewline;
    public SignificantCharsIterator(Iterator<Character> ic) {
        super(ic);
    }

    public static SignificantCharsIterator fromString(String s) {
        return new SignificantCharsIterator(new StringCharIterator(s));
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
        return super.hasNext();
    }

    Character noCommentNext() {
        if (this.ignoreComments()) {
            // virtual whitespace between comments to avoid a/*  */b becoming ab
            return ' ';
        }
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

    boolean ignoreComments() {
        boolean res = false;
        boolean ignored = true;
        while(ignored && super.hasNext()) {
            // loop in caso ci siamo più commenti diversi di fila
            ignored = false;
            if (super.startsWith(inlineCommentStart)) {
                ignorePastPrefix(newline);
                ignored = true;
                res = true;
            } 
            else if (super.startsWith(multilineCommentStart)) {
                ignorePastPrefix(multilineCommentEnd);
                ignored = true;
                res = true;
            }
        }
        return res;
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
