package org.jelly.lang.javaffi;

import java.lang.reflect.*;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

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
    private final static PrimitiveWrapperHandler wrap = new PrimitiveWrapperHandler();

    public static Result<Object, ReflectiveOperationException>
        tryCall(Object caller, String methName, Object[] args) {
        return tryClassObjectCall(caller.getClass(), caller, methName, args);
    }

    public static Result<Object, ReflectiveOperationException>
        tryCallStatic(Class<?> callerClass, String methName, Object[] args) {
        return tryClassObjectCall(callerClass, null, methName, args);
    }

    public static Object call(Object caller, String methName, Object[] args) {
        return switch (tryCall(caller, methName, args)) {
            case GoodResult<?, ?> g -> g.get();
            case BadResult<?, ?> b ->
                    throw new WrappedCallException("wrapped call to " + caller.getClass().getCanonicalName() + "." + methName + " failed", b.getThrowable());
        };
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


    private static Result<Object, ReflectiveOperationException>
        tryClassObjectCall(Class<?> callerClass, Object caller, String methName, Object[] args) {
        try {
            Method meth = lookupMethod(callerClass, methName, typeArray(args));
            return new GoodResult<>(meth.invoke(caller, args));
        }
        catch(ReflectiveOperationException ex) {
            // ex can be: no such method exception, illegal access exception, or invocation target exception
            return new BadResult<>(ex);
        }
    }

    private static Result<Object, ReflectiveOperationException>
        tryConstruct(Class<?> cls, Object[] args) {
        try {
            Constructor<?> c = lookupConstructor(cls, typeArray(args));
            return new GoodResult<>(c.newInstance(args));
        }
        catch(ReflectiveOperationException ex) {
            return new BadResult<>(ex);
        }
    }

    public static Object construct(Class<?> cls, Object[] args) {
        return switch(tryConstruct(cls, args)) {
            case GoodResult<?,?> good -> good.get();
            case BadResult<?,?> bad -> throw new WrappedCallException
                    ("could not call constructor to " + cls.getCanonicalName() +
                            " with arguments " + Arrays.toString(args) +
                            ", of types " + Arrays.toString(typeArray(args)),
                            bad.getThrowable());
        };
    }

    private static Class<?>[] typeArray(Object[] arr) {
        Class<?>[] paramTypes = new Class<?>[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            paramTypes[i] = arr[i].getClass();
        }
        return paramTypes;
    }

    // TODO da qui in giù fa tutto abbastanza schifo, ma non so come altro farlo
    private static Method lookupMethod(Class<?> cls, String meth, Class<?>[] paramTypes) throws NoSuchMethodException {
        try {
            return cls.getMethod(meth, paramTypes);
        } catch(NoSuchMethodException nse) {
            /* automatic (un)boxing of primitive types can mess with the exact type matches
             * required by getMethod()
             * fallback for such cases in which there was a matching method but none was returned
             * that is, we test different configurations of primitive types in the method
             * to see if any would have matched a method in the caller class
             */

            // first of all, is the fallback even justifiable?
            if(Arrays.stream(paramTypes).anyMatch(wrap::isPrimitiveWrapper)) {
                // then
                Method primitiveFall = getMethodPrimitiveFallback(cls, meth, paramTypes);
                if (primitiveFall != null)
                    return primitiveFall;

                Method slowFall = getMethodVerySlowFallback(cls, meth, paramTypes);
                if (slowFall != null)
                    return slowFall;
            }
            throw nse;
        }
    }

    private static Method getMethodPrimitiveFallback(Class<?> cls, String methName, Class<?>[] paramTypes) {
        try {
            return cls.getMethod(methName, primitiveTypeArray(paramTypes));
        } catch(NoSuchMethodException nse) {
            return null;
        }
    }

    private static Class<?>[] primitiveTypeArray(Class<?>[] clss) {
        Class<?>[] res = new Class<?>[clss.length];
        for(int i = 0; i<clss.length; ++i)
            res[i] = wrap.primitiveMaybe(clss[i]);
        return res;
    }

    private static Method getMethodVerySlowFallback(Class<?> cls, String methName, Class<?>[] paramTypes) {
        return Arrays.stream(cls.getMethods())
                    .filter(meth -> methodMatch(meth, methName, paramTypes))
                    .findFirst()
                    .orElse(null);
    }

    private static boolean methodMatch(Method m, String name, Class<?>[] paramTypes) {
        return m.getName().equals(name) && typesMatch(m.getParameterTypes(), paramTypes);
    }
    private static boolean typesMatch(Class<?>[] a, Class<?>[] b) {
        if(a.length != b.length)
            return false;
        for(int i = 0; i<a.length; ++i)
            if(wrap.primitiveMaybe(a[i]) != wrap.primitiveMaybe(b[i]))
                return false;
        return true;
    }

    private static Constructor<?> lookupConstructor(Class<?> cls, Class<?>[] paramTypes) throws NoSuchMethodException {
        try {
            return cls.getConstructor(paramTypes);
        } catch(NoSuchMethodException nse) {
            // fallback is the same as the lookupMethod() one
            // but I don't know how to dry the shared code between the two approaches
            if(Arrays.stream(paramTypes).anyMatch(wrap::isPrimitiveWrapper)) {
                Constructor<?> primitiveFall = getConstructorPrimitiveFallback(cls, paramTypes);
                if (primitiveFall != null)
                    return primitiveFall;

                Constructor<?> slowFall = getConstructorVerySlowFallback(cls, paramTypes);
                if (slowFall != null)
                    return slowFall;
            }

            throw nse;
        }
    }

    private static Constructor<?> getConstructorPrimitiveFallback(Class<?> cls, Class<?>[] paramTypes) {
        try {
            return cls.getConstructor(primitiveTypeArray(paramTypes));
        } catch (NoSuchMethodException nse) {
            return null;
        }
    }

    private static Constructor<?> getConstructorVerySlowFallback(Class<?> cls, Class<?>[] paramTypes) {
        return Arrays.stream(cls.getConstructors())
                .filter(a -> typesMatch(a.getParameterTypes(), paramTypes))
                .findFirst()
                .orElse(null);
    }
}

class PrimitiveWrapperHandler {
    /*
     * I'd like to call this an enterprise global variables
     */
    private static final Map<Class<?>, Class<?>> wrapToPrimitive = new HashMap<>();

    public PrimitiveWrapperHandler() {
        wrapToPrimitive.put(Boolean.class,  boolean.class);
        wrapToPrimitive.put(Character.class, char.class);
        wrapToPrimitive.put(Short.class, short.class);
        wrapToPrimitive.put(Integer.class, int.class);
        wrapToPrimitive.put(Long.class, long.class);
        wrapToPrimitive.put(Float.class, float.class);
        wrapToPrimitive.put(Double.class, double.class);
        wrapToPrimitive.put(Byte.class, byte.class);
    }

    public boolean isPrimitiveWrapper(Class<?> c) {
        return wrapToPrimitive.containsKey(c);
    }

    public Class<?> wrappedPrimitive(Class<?> cls) {
        Class<?> c = wrapToPrimitive.get(cls);
        if(c != null)
            return c;
        return cls;
    }

    public Class<?> primitiveMaybe(Class<?> cls) {
        Class<?> x = wrappedPrimitive(cls);
        if(x == null)
            return cls;
        return x;
    }
}