package org.jelly.parse.token;

import java.util.Arrays;
import java.util.Comparator;

public class StringUtils {
    public static String longestStartingPunctuation(String s, int i) {
        return Arrays.stream(Synthax.punctuationTokens)
                .filter(a -> s.startsWith(a, i))
                .max(Comparator.comparingInt(String::length)) // l'idea è stata di intellij
                .orElse(null);
    }

    public static boolean stringIsPunctuation(String s) {
        return Arrays.asList(Synthax.punctuationTokens).contains(s);
    }

    public static boolean stringStartsWithPunctuation(String s, int i) {
        return  Arrays.stream(Synthax.punctuationTokens).anyMatch(a -> s.startsWith(a, i));
    }
}
