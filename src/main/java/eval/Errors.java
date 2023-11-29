package eval;

public class Errors {
    public static void error(String s) {
        System.err.println("Error : " + s);
    }

    public static void warn(String s) {
        System.err.println("Warning : " + s);
    }
}
