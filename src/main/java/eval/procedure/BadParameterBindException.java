package eval.procedure;

public class BadParameterBindException extends RuntimeException {
    int expectedNArgs;
    int actualNArgs;
    Expectancy arraylistExpectancy;
    enum Expectancy {EXACTLY, AT_LEAST};

    BadParameterBindException(String s) {
        super(s);
    }

    BadParameterBindException(int nExpected, int nActual, Expectancy exp) {
        super("function called with " + nActual + " arguments but wanted " +
                switch(exp) { case EXACTLY -> "exactly"; case AT_LEAST -> "at least"; } +
                nExpected + " arguments");

        this.expectedNArgs = nExpected;
        this.actualNArgs = nActual;
        this.arraylistExpectancy = exp;
        // shove some random stats about the call in the exception
        // in case
    }
}
