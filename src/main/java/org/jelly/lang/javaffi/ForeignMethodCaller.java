package org.jelly.lang.javaffi;

import org.jelly.utils.ArrayUtils;

import java.lang.reflect.*;

import java.util.Arrays;

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
 * generics might fuck with jelly's (inexistant) interface to the java typesystem
 * replace result<T,E> with result { Object res; Throwable caught; } if all goes shitface
 */

public class ForeignMethodCaller {
    private final static PrimitiveWrapperHandler wrap = new PrimitiveWrapperHandler();

    public static Object call(Object caller, String methName, Object[] args) {
        return switch (tryCall(caller, methName, args)) {
            case GoodResult<?, ?> g -> g.get();
            case BadResult<?, ?> b ->
                    throw new WrappedCallException("wrapped call to " + caller.getClass().getCanonicalName() + "." + methName + " failed", b.getThrowable());
        };
    }

    public static Result<Object, Throwable>
    tryCall(Object caller, String methName, Object[] args) {
        return tryClassObjectCall(caller.getClass(), caller, methName, args);
    }

    public static Object callStatic(Class<?> callerClass, String methName, Object[] args) {
        return switch (tryCallStatic(callerClass, methName, args)) {
            case GoodResult<?, ?> g -> g.get();
            case BadResult<?, ?> b ->
                    throw new WrappedCallException
                            ("wrapped call to " + callerClass.getCanonicalName() +
                                    "." + methName + " failed",
                                    b.getThrowable());
        };
    }

    public static Result<Object, Throwable>
    tryCallStatic(Class<?> callerClass, String methName, Object[] args) {
        return tryClassObjectCall(callerClass, null, methName, args);
    }

    private static Result<Object, Throwable>
        tryClassObjectCall(Class<?> callerClass, Object caller, String methName, Object[] args) {
        try {
            Method meth = MethodFinder.findMethod(callerClass, methName, ArrayUtils.typeArray(args));
            return new GoodResult<>(meth.invoke(caller, args));
        }
        catch(InvocationTargetException ite) {
            return new BadResult<>(ite.getCause());
        }
        catch(Throwable t) {
            // ex can be: no such method exception, illegal access exception, or invocation target exception
            return new BadResult<>(t);
        }
    }

    public static Object construct(Class<?> cls, Object[] args) {
        return switch(tryConstruct(cls, args)) {
            case GoodResult<?,?> good -> good.get();
            case BadResult<?,?> bad -> throw new WrappedCallException
                    ("could not call constructor to " + cls.getCanonicalName() +
                            " with arguments " + Arrays.toString(args) +
                            ", of types " + Arrays.toString(ArrayUtils.typeArray(args)),
                            bad.getThrowable());
        };
    }

    private static Result<Object, Throwable>
    tryConstruct(Class<?> cls, Object[] args) {
        try {
            Constructor<?> c = MethodFinder.findConstructor(cls, ArrayUtils.typeArray(args));
            return new GoodResult<>(c.newInstance(args));
        }
        catch(Throwable t) {
            return new BadResult<>(t);
        }
    }
}