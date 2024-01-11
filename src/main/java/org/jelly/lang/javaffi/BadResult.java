package org.jelly.lang.javaffi;

public final class BadResult<T, E extends Throwable> implements Result<T, E> {
    private final E error;
    public BadResult(E error) {
        this.error = error;
    }
    @Override
    public boolean isGood() {
        return false;
    }

    @Override
    public T get() {
        throw new NotAGoodResultException("result is not good, cannot extract value");
    }

    @Override
    public String getErrorMessage() {
        return error.getMessage();
    }

    @Override
    public Throwable getCause() {
        return error.getCause();
    }

    @Override
    public StackTraceElement[] getStackTrace () {
        return error.getStackTrace();
    }

    public Throwable getThrowable () {
        return error;
    }
}
