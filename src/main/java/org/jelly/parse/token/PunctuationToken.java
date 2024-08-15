package org.jelly.parse.token;

import java.util.Map;
import java.util.HashMap;

public final class PunctuationToken extends Token {
    public enum PunctuationType {
        PAREN_OPEN, PAREN_CLOSE, SQUARE_OPEN, SQUARE_CLOSE, CURLY_OPEN, CURLY_CLOSE,
        COMMA, QUOTE, BACKTICK,
        COLON, DOUBLE_COLON, TRIPLE_COLON // added just to make the test cases harder
    };
    private static final PunctuationTypeTeller ptl = new PunctuationTypeTeller();

    private final PunctuationType type;

    public PunctuationToken(String s) {
        super(s);
        this.type = ptl.tell(s);
    }

    public PunctuationType getPunctuationType() {
        return type;
    }

    @Override
    public String toString() {
        return "PunctuationToken(\"" + getString() + "\")";
    }
}

final class PunctuationTypeTeller {
    private static final Map<String, PunctuationToken.PunctuationType> stringType = new HashMap<>();
    PunctuationTypeTeller() {
        stringType.put("(", PunctuationToken.PunctuationType.PAREN_OPEN);
        stringType.put(")", PunctuationToken.PunctuationType.PAREN_CLOSE);
        stringType.put("[", PunctuationToken.PunctuationType.SQUARE_OPEN);
        stringType.put("]", PunctuationToken.PunctuationType.SQUARE_CLOSE);
        stringType.put("{", PunctuationToken.PunctuationType.CURLY_OPEN);
        stringType.put("}", PunctuationToken.PunctuationType.CURLY_CLOSE);
        stringType.put(",", PunctuationToken.PunctuationType.COMMA);
        stringType.put("'", PunctuationToken.PunctuationType.QUOTE);
        stringType.put("`", PunctuationToken.PunctuationType.BACKTICK);
        stringType.put(":", PunctuationToken.PunctuationType.COLON);
        stringType.put("::", PunctuationToken.PunctuationType.DOUBLE_COLON);
        stringType.put(":::", PunctuationToken.PunctuationType.TRIPLE_COLON);
    }

    PunctuationToken.PunctuationType tell(String s) {
        return stringType.get(s);
    }
}
