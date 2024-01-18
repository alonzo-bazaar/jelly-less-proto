package org.jelly.parse.token;

/**
 * degenerazione naturale del concetto di functoide
 * questa classe è un namespace di variabili globali che ci ha provato troppo
 */
public class Synthax {
    // integers and floats (to be put in another class once I make that other class)
    static final String[] punctuationTokens = {":::", "::", ":", "(", ")", ",", "`", "'"};

    // TODO use these to determine comment checks, and add any other "magic string" to this class
    public static String inlineCommentBegin = ";";
    public static String multilineCommentBegin = "#|";
    public static String multilineCommentEnd = "|#";

    static Character stringSpecialCharacter(String ch) {
        return switch(ch) {
            case "Space" -> ' ';
            case "Newline" -> '\n';
            case "Return" -> '\r';
            case "Tab" -> '\t';
            case "Null" -> '\0';
            default -> null;
        };
    }

    static String stringEscapePrefix(String s, int i) {
        if(s.startsWith("\\\\", i))
            return "\\";
        if(s.startsWith("\\\"", i))
            return "\"";
        if(s.startsWith("\\r", i))
            return "\r";
        if(s.startsWith("\\n", i))
            return "\n";
        if(s.startsWith("\\t", i))
            return "\t";
        else return null;
    }

    public static boolean stringIsInteger(String s) {
        int i = 0;
        if(s.charAt(0) == '-')
            i = 1;
        if(i == s.length())
            return false;
        while(i != s.length()) {
            if(!charIsDigit(s.charAt(i)))
                return false;
            i++;
        }
        return true;
    }

    public static boolean stringIsFloat(String s) {
        int i = 0;
        boolean dot = false;
        if(s.charAt(0) == '-')
            i = 1;
        if(i == s.length())
            return false;
        while(i != s.length()) {
            if(charIsDigit(s.charAt(i)))
                i++;
            else {
                if(!dot && s.charAt(i) == '.') {
                    dot = true;
                    i++;
                }
                else
                    return false;
            }
        }
        return dot;
    }

    private static boolean charIsDigit(char c) {
        return c >= '0' && c<='9';
    }
}
