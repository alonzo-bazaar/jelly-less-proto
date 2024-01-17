package org.jelly.utils;

import org.jelly.parse.expression.NewExpressionIterator;
import org.jelly.parse.token.NewTokenIterator;

/**
 * utilities used in debug and test code
 * there are several pieces of code that are often reused for such purposes,
 * this class has been made to avoid littering test code with such utilities
 */
public class DebuggingUtils {

    // utilities
    public static String renderDebugString(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
            case '\n':
                sb.append("<\\n>");
                break;
            case '\t':
                sb.append("<\\t>");
                break;
            case '\r':
                sb.append("<\\r>");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // some types are weird to render for error messages, so
    public static String debugRender(Object o) {
        return switch(o) {
        case String s -> renderDebugString(s);
        default -> o.toString();
        };
    }

    public static NewTokenIterator tokensFromStrings(String... lines) {
        return new NewTokenIterator(new StringArrIterator(lines));
    }

    public static NewExpressionIterator expressionsFromStrings(String... lines) {
        return new NewExpressionIterator(tokensFromStrings(lines));
    }
}
