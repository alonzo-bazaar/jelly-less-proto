package org.jelly.eval;

public class ErrorFormatter {
    public static void error(String s) {
        System.err.println("Error : " + s);
    }

    public static void warn(String s) {
        System.err.println("Warning : " + s);
    }
}
