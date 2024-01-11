import java.lang.reflect.*;
import java.security.InvalidParameterException;
/**
 * API Desiderata per lisp
 *
 * "raw" call methods, fail if underlying method throws exception
 * (call object method . args)
 * (callStatic class method . args)
 *
 * "try" call methods, return a result object which can be tested for good/bad and "unwrapped" a la rust
 * (tryCall object method . args)
 * (tryCallStatic class method . args)
 *
 * POTENTIAL TODO
 * generics might fuck with jelly's interface to the java typesystem
 * replace result<T,E> with result { Object res; Throwable caught; } if all goes shitface
 */

public class ForeignMethodCaller {
    public static ForeignCallResult<Object, ReflectiveOperationException>
        tryCall(Object caller, String methName, Object[] args) {
        return tryClassObjectCall(caller.getClass(), caller, methName, args);
    }

    public static ForeignCallResult<Object, ReflectiveOperationException>
        tryCallStatic(Class<?> callerClass, String methName, Object[] args) {
        return tryClassObjectCall(callerClass, null, methName, args);
    }

    public static Object call(Object caller, String methName, Object[] args) {
        ForeignCallResult<?,?> res = tryClassObjectCall(caller.getClass(), caller, methName, args);
        switch(res) {
        case Good<?,?> g:
            return g.get();
        case Bad<?,?> b:
            throw new WrappedCallException("wrapped call to " + caller.getClass().getCanonicalName() + "." + methName + " failed", b.getThrowable());
        }
    }

    public static Object callStatic(Class<?> callerClass, String methName, Object[] args) {
        ForeignCallResult<?,?> res = tryClassObjectCall(callerClass, null, methName, args);
        switch(res) {
        case Good<?,?> g:
            return g.get();
        case Bad<?,?> b:
            throw new WrappedCallException("wrapped call to " + callerClass.getCanonicalName() + "." + methName + " failed", b.getThrowable());
        }
    }

    private static ForeignCallResult<Object, ReflectiveOperationException>
        tryClassObjectCall(Class<?> callerClass, Object caller, String methName, Object[] args) {
        try {
            Class<?> paramTypes[] = new Class<?>[args.length];
            for (int i = 0; i < args.length; ++i) {
                paramTypes[i] = args[i].getClass();
            }
            Method meth = callerClass.getMethod(methName, paramTypes);
            return new Good<Object, ReflectiveOperationException>(meth.invoke(caller, args));
        }
        catch(ReflectiveOperationException ex) {
            // ex can be either
            // no such method exception
            // illegal access exception
            // invocation target exception
            return new Bad<Object, ReflectiveOperationException>(ex);
        }
    }

    public static Object[] of(Object... args) {
        return args;
    }

    public static void main(String args[]) {
        // i metodi invocati da call possono avere come parametri solo oggetti
        // purtroppo la reflection with primitivi does shit to a mf
        // (probabile c'è il modo per farlo ma lasciamo fare per adesso)
        LabRat lr = new LabRat(10);

        System.out.println(call(lr, "add", of(10)));
        System.out.println(call(lr, "sub", of(10)));
        System.out.println(call(lr, "get", of()));

        System.out.println(callStatic(LabRat.class, "waluigi", of()));
        System.out.println(callStatic(LabRat.class, "waluigi", of()));
        System.out.println(callStatic(LabRat.class, "waluigi", of()));
    }
}
class NotAnErrorException extends RuntimeException {
    public NotAnErrorException(String s) {
        super(s);
    }
}

class NotAGoodResultException extends RuntimeException {
    public NotAGoodResultException(String s) {
        super(s);
    }
}

class WrappedCallException extends RuntimeException {
    public WrappedCallException(String s, Throwable cause) {
        super(s, cause);
    }
}

abstract sealed class ForeignCallResult<T, E extends Throwable> permits Good, Bad {
    public abstract boolean isGood();
    public abstract T get();
    public abstract String getErrorMessage();
    public abstract Throwable getCause();
    public abstract StackTraceElement[] getStackTrace ();
}

final class Good<T, E extends Throwable> extends ForeignCallResult<T, E> {
    private final T val;
    public Good(T val) {
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

final class Bad<T, E extends Throwable> extends ForeignCallResult<T, E> {
    private final E error;
    public Bad(E error) {
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


class LabRat {
    private int x;
    public LabRat(int x) {
        System.out.println("initializing!");
        this.x = x;
    }
    public int add(Integer y) {
        System.out.println("adding!");
        this.x += y;
        return this.x;
    }
    public int sub(Integer y) {
        System.out.println("subtracting!");
        this.x -=y;
        return this.x;
    }

    public int get() {
        System.out.println("getting!");
        return this.x;
    }

    static int counter = 0;
    public static int waluigi() {
        return ++counter;
    }
}
