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
        return res;
    }

    String extractNextToken() {
        chars.ignoreCommentsAndWhitespace();
        // guarda, vaffanculo
        // now the stream begins with the token, so
        if (this.startsWithSpecial())
            return this.nextSpecial();

        else if (this.chars.startsWith("\"")) 
            return this.nextString();
        else
            return this.nextNormal();
    }

    String nextNormal() {
        StringBuffer sb = new StringBuffer();
        // TODO extract common stopping conditions to better their extensibility
        while (chars.hasNext() &&
               !chars.startsWithWhitespace() &&
               !chars.startsWith("\"") &&
               !this.startsWithSpecial()) {
            sb.append(chars.next());
        }
        return sb.toString();
    }

    String nextString() {
        StringBuffer sb = new StringBuffer();
        // opening '"'
        sb.append(chars.next());
        String[] escapes = {"\\\"", "\\\t", "\\\n"}; // escape explosion incoming
        while (chars.hasNext() && !chars.startsWith("\"")) {
            for (String s : escapes) {
                if (chars.startsWith(s)) {
                    sb.append(s);
                    chars.ignorePastPrefix(s);
                }
            }
            if(chars.hasNext() && !chars.startsWith("\""))
                sb.append(chars.next());
        }
        // closing '"'
        sb.append(chars.next());
        return sb.toString();
    }

    String nextSpecial() {
        StringBuffer sb = new StringBuffer();
        while (chars.hasNext() &&
               !chars.startsWith("\"") &&
               !chars.startsWithWhitespace() &&
               this.specialStartsWith(sb.toString().concat(chars.getPrefix(1)))) {
            sb.append(chars.next());
        }
        return sb.toString();
    }

    @Override
    public boolean hasNext() {
        chars.ignoreCommentsAndWhitespace();
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
