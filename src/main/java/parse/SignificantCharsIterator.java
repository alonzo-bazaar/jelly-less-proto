package parse;


import java.util.Iterator;

import utils.PrefixIterator;

public class SignificantCharsIterator implements Iterator<Character>{
    // commenti stile java/c++ per non scazzare troppe abitudini (altrui)
    private static String inlineCommentStart = "//";
    private static String multilineCommentStart = "/*";
    private static String multilineCommentEnd = "*/";


    private static String unixNewline = "\n";
    private static String dosNewline = "\r\n";

    private String newline;
    PrefixIterator chars;
    public SignificantCharsIterator(Iterator<Character> ic) {
        chars = new PrefixIterator(ic);
        /* un piccolo tentativo a fare cross platform */
        if (isWindows())
            this.newline = dosNewline;
        else 
            this.newline = unixNewline;
    }

    private static boolean isWindows() {
        /* this function does not the fuck belong here
         * copy  https://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java somewhere */
        return (System.getProperty("os.name").startsWith("Windows"));
    }

    @Override
    public Character next() {
        ignoreComments();
        return chars.next();
    }

    @Override
    public boolean hasNext() {
        ignoreComments();
        return chars.hasNext();
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

    public void ignoreComments() {
        boolean ignored = true;
        while(ignored && chars.hasNext()) {
            // loop in caso ci siamo più commenti diversi di fila
            ignored = false;
            if (chars.startsWith(inlineCommentStart)) {
                ignorePastPrefix(newline);
                ignored = true;
            } 
            else if (chars.startsWith(multilineCommentStart)) {
                ignorePastPrefix(multilineCommentEnd);
                ignored = true;
            }
        }
    }

    public void ignoreUntilPrefix(String prefix) {
        while (chars.hasNext() && !chars.startsWith(prefix)) {
            chars.next();
        }
    }

    public void ignorePastPrefix(String prefix) {
        ignoreUntilPrefix(prefix);
        // but we shall also ignore the prefix
        for (int i = 0; i < prefix.length() && chars.hasNext() ; ++i) {
            chars.next();
        }
    }
}
