package parse;

import java.util.Iterator;

public class TokenIterator implements Iterator<String>{
    private static String[] specialTokens = {
        "(", ")", "'",
        ";", ";;", ";;;", // aggiuti così solo per fare lo stronzo
        "#|", "|#",
        "#\\",
    };
    private SignificantCharsIterator chars;
    // tenere a mente che per "definizione" SignificantCharsIterator
    // non ha mai due spazii bianchi di fila
    public TokenIterator(SignificantCharsIterator chars) {
        this.chars = chars;
    }

    @Override
    public String next() {
        String res = extractNextToken();
        skipWhite();
        return res;
    }

    void skipWhite() {
        /* versione fatta un po' alla cazzo, fatta per accertarsi che
         * lastWasWhite venga aggiornato per tutti i caratteri, anche skippati
         */
        while(chars.hasNext() && chars.startsWithWhitespace())
            chars.next();
    }

    String extractNextToken() {
        if (this.startsWithSpecial())
            return this.nextSpecial();
        else
            return this.nextNormal();
    }

    String nextNormal() {
        StringBuffer sb = new StringBuffer();
        while (chars.hasNext() &&
               !chars.startsWithWhitespace()
               && !this.startsWithSpecial()) {
            sb.append(chars.next());
        }
        return sb.toString();
    }

    String nextSpecial() {
        StringBuffer sb = new StringBuffer();
        while (chars.hasNext() &&
               !chars.startsWithWhitespace() &&
               this.specialStartsWith(sb.toString().concat(chars.getPrefix(1)))) {
            sb.append(chars.next());
        }
        return sb.toString();
    }

    @Override
    public boolean hasNext() {
        return chars.hasNext();
    }

    boolean startsWithSpecial() {
        for (String s : specialTokens) {
            if (chars.startsWith(s)) {
                return true;
            }
        }

        return false;
    }

    boolean specialStartsWith(String pref) {
        for (String s : specialTokens) {
            if (s.startsWith(pref)) {
                return true;
            }
        }

        return false;
    }
}
