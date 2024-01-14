package org.jelly.parse.token;

import java.util.Arrays;
import java.util.Comparator;

public class StringUtils {
    // integers and floats (to be put in another class once I make that other class)
    private static final String[] punctuationTokens = {":::", "::", ":", "(", ")", ",", "`", "'"};
    public static boolean stringIsInteger(String s) {
        int i = 0;
        if(s.charAt(0) == '-')
            i = 1;
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

    public static boolean stringIsPunctuation(String s) {
        return Arrays.asList(punctuationTokens).contains(s);
    }

    public static boolean stringStartsWithPunctuation(String s, int i) {
        return  Arrays.stream(punctuationTokens).anyMatch(a -> s.startsWith(a, i));
    }

    public static String longestStartingPunctuation(String s, int i) {
        return Arrays.stream(punctuationTokens)
                .filter(a -> s.startsWith(a, i))
                .max(Comparator.comparingInt(String::length)) // l'idea è stata di intellij
                .orElse(null);
    }
}
