package org.jelly.lang.javaffi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveWrapperHandler {
    /*
     * I'd like to call this an enterprise global variables
     */
    private final Map<Class<?>, Class<?>> wrapToPrimitive = new HashMap<>();

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

    public Class<?> primitiveMaybe(Class<?> cls) {
        return switch(wrapToPrimitive.get(cls)) {
            case null -> cls;
            case Class<?> c -> c;
        };
    }

    public boolean anyPrimitive(Class<?>[] clss) {
        return Arrays.stream(clss).anyMatch(wrapToPrimitive::containsKey);
    }

    public Class<?>[] toPrimitiveArray(Class<?>[] clss) {
        Class<?>[] res = new Class<?>[clss.length];
        for(int i = 0; i<clss.length; ++i) {
            res[i] = primitiveMaybe(clss[i]);
        }
        return res;
    }

    public boolean isAssignable(Class<?> cls, Class<?> from) {
        return cls.isAssignableFrom(from) || (primitiveMaybe(cls).equals(primitiveMaybe(from)));
    }
}