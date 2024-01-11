package org.jelly.lang.javaffi;

public final class GoodResult<T, E extends Throwable> implements Result<T, E> {
    private final T val;
    public GoodResult(T val) {
        this.val = val;
    }

    @Override
    public boolean isGood() {
        return true;
    }

    @Override
    public T get() {
        return val;
    }

    @Override
    public String getErrorMessage() {
        throw new NotAnErrorException
            ("result is not an error result, cannot get error message");
    }

    @Override
    public Throwable getCause() {
        throw new NotAnErrorException
            ("result is not an error result, cannot get error cause");
    }

    @Override
    public StackTraceElement[] getStackTrace () {
        throw new NotAnErrorException
            ("result is not an error result, cannot get error stack trace");
    }
}
