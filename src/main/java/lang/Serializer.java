package lang;

public class Serializer {
    public static Object fromToken(String token) {
        if (token.isEmpty()) {
            // le famo tirare?
            return Constants.NIL;
        }
        else if (token.equalsIgnoreCase("nil")) {
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
        return token.chars().allMatch(Character::isDigit);
    }

    static boolean allNumericOrDot(String token) {
        return token.chars().filter(c -> c == '.').count() == 1 &&
            token.chars().filter(c -> c != '.').allMatch(Character::isDigit);
    }

    static Integer intFromToken(String token) {
        return Integer.parseInt(token, 10);
    }

    static String stringFromToken(String token) {
        // token will be "<contents>", we just want <contents>
        // so we remove the first and last characters
        return token.subSequence(1, token.length() - 1).toString();
    }

    static Character charFromToken(String token) {
        // token will be #\<char>
        // we just want <char>
        // we're not gonna consider things like #\Space or #\Newline, for now
        return token.charAt(2);
    }

    static Double doubleFromToken(String token) {
        return Double.parseDouble(token);
    }
}
