package parse;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class TokenIterator implements Iterator<String> {
    private static final String[] specialTokens = {
        "(", ")", "'",
        "#'",
        ":", "::", ":::",
    };

    Map<String, String> escapes = new HashMap<String, String>();
    private final SignificantCharsIterator chars;

    public TokenIterator(SignificantCharsIterator chars) {
        this.chars = chars;
        this.escapes.put("\\\\", "\\");
        this.escapes.put("\\\"", "\"");
        this.escapes.put("\\t", "\t");
        this.escapes.put("\\n", "\n");
    }

    public static TokenIterator fromString(String s) {
        return new TokenIterator(SignificantCharsIterator.fromString(s));
    }

    @Override
    public String next() {
        return extractNextToken();
    }

    String extractNextToken() {
        chars.ignoreCommentsAndWhitespace();
        // now the stream begins with the token, so
        if (this.startsWithString())
            return this.nextString();
        if (this.startsWithChar())
            return this.nextChar();
        if (this.startsWithSpecial())
            return this.nextSpecial();
        return this.nextNormal();
    }

    boolean startsWithLiteral() {
        return this.startsWithChar() || this.startsWithString();
    }

    boolean startsWithString() {
        return chars.startsWith("\"");
    }
    boolean startsWithChar() {
        return chars.startsWith("#\\");
    }

    String nextNormal() {
        StringBuilder sb = new StringBuilder();
        while (chars.hasNext()
                && !chars.startsWithWhitespace()
                && !chars.startsWithComment()
                && !this.startsWithLiteral()
                && !this.startsWithSpecial()) {
            sb.append(chars.next());
        }
        return sb.toString();
    }

    String nextString() {
        StringBuilder sb = new StringBuilder();
        // opening '"'
        sb.append(chars.next());
        while (chars.hasNext() && !chars.startsWith("\"")) {
            String startingEscape = escapes.keySet()
                .stream()
                .filter(chars::startsWith)
                .findFirst()
                .orElse(null);
            if(startingEscape != null) {
                sb.append(escapes.get(startingEscape));
                chars.ignorePastPrefix(startingEscape);
            }
            else {
                sb.append(chars.next());
            }
        }
        // closing '"'
        sb.append(chars.next());
        return sb.toString();
    }

    String nextSpecial() {
        StringBuilder sb = new StringBuilder();
        while (chars.hasNext()
                && !chars.startsWithWhitespace()
                && !chars.startsWithComment()
                && !this.startsWithLiteral()
                && this.specialStartsWith(sb.toString().concat(chars.getPrefix(1)))) {
            sb.append(chars.next());
        }
        return sb.toString();
    }

    String nextChar() {
        chars.ignorePastPrefix("#\\");
        char c = chars.next();
        return String.valueOf(c);
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
