package lang;

public class Serializer {
    public static LispExpression fromToken(String token) {
        if (token.length() == 0) {
            // le famo tirare?
            return Constants.NIL;
        }
        else if (token.toLowerCase().equals("nil")) {
            return Constants.NIL;
        }
        else if (token.startsWith("#\\")) {
            return charFromToken(token);
        }
        else if (allNumeric(token)) {
            return intFromToken(token);
        }
        else if (allNumericOrDot(token)) {
            return doubleFromToken(token);
        }
        else if (token.startsWith("\"")) {
            return stringFromToken(token);
        }
        else {
            return new LispSymbol(token);
        }
    }
    
    static boolean allNumeric(String token) {
        return token.chars().allMatch(c -> Character.isDigit(c));
    }

    static boolean allNumericOrDot(String token) {
        return token.chars().filter(c -> c == '.').count() == 1 &&
            token.chars().filter(c -> c != '.').allMatch(c -> Character.isDigit(c));
    }

    static LispValue<Integer> intFromToken(String token) {
        return new LispValue<Integer>(Integer.parseInt(token, 10));
    }

    static LispValue<String> stringFromToken(String token) {
        // token will be "<contents>", we just want <contents>
        // so we remove the first and last characters
        return new LispValue<String>(token
                              .subSequence(1, token.length() - 1)
                              .toString());
    }

    static LispValue<Character> charFromToken(String token) {
        // token will be #\<char>
        // we just want <char>
        // we're not gonna consider things like #\Space or #\Newline, for now
        return new LispValue<Character>(token.charAt(2));
    }

    static LispExpression doubleFromToken(String token) {
        return new LispValue<Double>(Double.parseDouble(token));
    }
}
