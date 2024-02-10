package org.jelly.utils;

import org.jelly.parse.syntaxtree.SyntaxTreeIterator;
import org.jelly.parse.token.TokenIterator;

public class DebuggingUtils {

    public static TokenIterator tokensFromStringArr(String[] ss) {
        return new TokenIterator(new StringArrIterator(ss));
    }
    public static TokenIterator tokensFromStrings(String... args) {
        return tokensFromStringArr(args);
    }
    public static SyntaxTreeIterator expressionsFromStringArr(String[] ss) {
        return new SyntaxTreeIterator(tokensFromStringArr(ss));
    }
    public static SyntaxTreeIterator expressionsFromStrings(String... args) {
        return expressionsFromStringArr(args);
    }


    public static String debugRender(Object o) {
        return switch(o) {
            case String s -> debugRenderString(s);
            default -> o.toString();
        };
    }

    private static String debugRenderString(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<s.length(); ++i) {
            char c = s.charAt(i);
            switch(c) {
                case '\n' -> sb.append("<\\n>");
                case '\t' -> sb.append("<\\t>");
                case '\r' -> sb.append("<\\r>");
                case '\\' -> sb.append("<\\\\>");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
