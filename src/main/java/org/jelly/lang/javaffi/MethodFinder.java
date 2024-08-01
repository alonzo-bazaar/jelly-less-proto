package org.jelly.lang.javaffi;

import org.jelly.eval.ErrorFormatter;
import org.jelly.lang.data.Cons;
import org.jelly.utils.ArrayUtils;
import org.jelly.utils.Pair;
import org.jelly.utils.Triple;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MethodFinder {
    private static final Map<Triple<Class<?>, String, Class<?>[]>, Method> methodCache = new ConcurrentHashMap<>();
    private static final Map<Pair<Class<?>, Class<?>[]>, Constructor<?>> constructorCache = new ConcurrentHashMap<>();
    private static final PrimitiveWrapperHandler prim = new PrimitiveWrapperHandler();

    public static Method findMethod(Class<?> cls, String methName, Class<?>[] params) throws NoSuchMethodException {
        if(methodCache.containsKey(Triple.of(cls, methName, params))) {
            return methodCache.get(Triple.of(cls, methName, params));
        }

        Method m = findMethodNoCache(cls, methName, params);
        methodCache.put(Triple.of(cls, methName, params), m);
        return m;
    }

    private static Method findMethodNoCache(Class<?> cls, String methName, Class<?>[] params) throws NoSuchMethodException {
        try {
            return cls.getMethod(methName, params);
        } catch(NoSuchMethodException noMeth) {
            Optional<Method> maybe = findMethodFallback(cls, methName, params);
            if(maybe.isPresent()) {
                return maybe.get();
            }
            if(prim.anyPrimitive(params)) {
                Optional<Method> maybePrimitive = findMethodFallback(cls, methName, prim.toPrimitiveArray(params));
                if (maybePrimitive.isPresent()) {
                    return maybePrimitive.get();
                }
            }
            throw new NoSuchMethodException("no method matches "
                    + cls.getCanonicalName() + "." + methName + "(" + ArrayUtils.renderArr(Arrays.stream(params).map(Class::getCanonicalName).toArray(), ", ") + ")");
        }
    }

    private static Optional<Method> findMethodFallback(Class<?> cls, String methName, Class<?>[] params) {
        List<Method> matches = Arrays.stream(cls.getMethods())
                .filter(a -> (a.getName().equals(methName))
                                && (a.getParameterTypes().length == params.length)
                                && zip(a.getParameterTypes(), params)
                                .stream()
                                .allMatch(pair -> prim.isAssignable(pair.first, pair.second))) // pair.first.isAssignableFrom(pair.second)))
                .toList();
        return switch (matches.size()) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(matches.getFirst());
            default -> {
                ErrorFormatter.warn("more than one match for method" + cls.getCanonicalName() +
                        "." + methName +"(" + ArrayUtils.renderArr(Arrays.stream(params).map(Class::getCanonicalName).toArray(), ", ") + ")");
                yield Optional.of(matches.getFirst());
            }
        };
    }

    private static <A, B> List<Pair<A,B>> zip(A[] a, B[] b) throws InvalidParameterException {
        if(a.length != b.length)
            throw new InvalidParameterException("arrays must be of same size to zip");

        List<Pair<A,B>> res = new ArrayList<>(a.length);
        for(int i = 0; i<a.length; ++i) {
            res.add(Pair.of(a[i], b[i]));
        }
        return res;
    }

    public static Constructor<?> findConstructor(Class<?> cls, Class<?>[] params) throws NoSuchMethodException {
        if(constructorCache.containsKey(Pair.of(cls, params))) {
            return constructorCache.get(Pair.of(cls, params));
        }
        Constructor<?> c = findConstructorNoCache(cls, params);
        constructorCache.put(Pair.of(cls, params), c);
        return c;
    }

    private static Constructor<?> findConstructorNoCache(Class<?> cls, Class<?>[] params) throws NoSuchMethodException {
        try {
            return cls.getConstructor(params);
        } catch(NoSuchMethodException noMeth) {
            Optional<Constructor<?>> maybe = findConstructorFallback(cls, params);
            if(maybe.isPresent()) {
                return maybe.get();
            }
            if(prim.anyPrimitive(params)) {
                Optional<Constructor<?>> maybePrimitive = findConstructorFallback(cls, prim.toPrimitiveArray(params));
                if (maybePrimitive.isPresent()) {
                    return maybePrimitive.get();
                }
            }
            throw new NoSuchMethodException("no constructor matches "
                    + cls.getCanonicalName() + "(" + ArrayUtils.renderArr(Arrays.stream(params).map(Class::getCanonicalName).toArray(), ", ") + ")");
        }
    }

    private static Optional<Constructor<?>> findConstructorFallback(Class<?> cls, Class<?>[] params) throws NoSuchMethodException {
        List<Constructor<?>> matches = Arrays.stream(cls.getConstructors())
                .filter(c -> (c.getParameterTypes().length == params.length)
                            && zip(c.getParameterTypes(), params)
                        .stream()
                        .allMatch(pair -> prim.isAssignable(pair.first, pair.second))) // pair.first.isAssignableFrom(pair.second)))
                .toList();
        return switch (matches.size()) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(matches.getFirst());
            default -> {
                ErrorFormatter.warn("more than one constructor matches " + cls.getCanonicalName() +
                        "(" + ArrayUtils.renderArr(Arrays.stream(params).map(Class::getCanonicalName).toArray(), ", ") + ")");
                yield Optional.of(matches.getFirst());
            }
        };
    }
}
