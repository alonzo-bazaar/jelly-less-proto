package org.jelly.lang.javaffi;

public sealed interface Result<T, E extends Throwable> permits GoodResult, BadResult {
    boolean isGood();
    T get();
    String getErrorMessage();
    Throwable getCause();
    StackTraceElement[] getStackTrace();
}
