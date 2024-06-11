package org.jelly.lang.arith;
import java.util.Arrays;

import org.jelly.lang.errors.InvalidTypeException;

public class Arith {

    private static boolean isFixed(Number n) {
        return switch(n) {
            case Byte b -> true;
            case Short s -> true;
            case Integer i -> true;
            case Long l -> true;
            default -> false;
        };
    }

    private static boolean allFixed(Number... ns) {
        return Arrays.stream(ns).allMatch(Arith::isFixed);
    }

    private static boolean isFloating(Number n) {
        return !isFixed(n);
    }
    public static Number add(Number a, Number b) {
        if(isFixed(a) && isFixed(b))
            return (Integer)(a.intValue() + b.intValue());
        else
            return (Double)(a.doubleValue() + b.doubleValue());
    }

    public static Number subtract(Number a, Number b) {
        if(isFixed(a) && isFixed(b))
            return (Integer)(a.intValue() - b.intValue());
        else
            return (Double)(a.doubleValue() - b.doubleValue());
    }

    public static Number multiply(Number a, Number b) {
        if(isFixed(a) && isFixed(b))
            return (Integer)(a.intValue() * b.intValue());
        else
            return (Double)(a.doubleValue() * b.doubleValue());
    }

    public static Number divide(Number a, Number b) {
        return (Double)(a.doubleValue() / b.doubleValue());
    }

    public static Integer modulo(Number a, Number b) {
        if(isFloating(a))
            throw new InvalidTypeException("cannot compute [[" + a + "]%[" + b + "] because first operand [" + a + "] is not of an integer type");
        else if(isFloating(b))
            throw new InvalidTypeException("cannot compute [[" + a + "]%[" + b + "] because the second operand [" + b + "] is not of an integer type");
        else
            return (a.intValue() % b.intValue());
    }

    public static boolean lessThan(Number a, Number b) {
        if(isFixed(a) && isFixed(b))
            return a.intValue() < b.intValue();
        else
            return a.doubleValue() < b.doubleValue();
    }

    public static boolean greaterThan(Number a, Number b) {
        if(isFixed(a) && isFixed(b))
            return a.intValue() > b.intValue();
        else
            return a.doubleValue() > b.doubleValue();
    }

    public static boolean equalTo(Number a, Number b) {
        if(isFixed(a) && isFixed(b))
            return a.intValue() == b.intValue();
        else
            return a.doubleValue() == b.doubleValue();
    }

    public static boolean lessEqual(Number a, Number b) {
        return lessThan(a,b) || equalTo(a,b);
    }

    public static boolean greaterEqual(Number a, Number b) {
        return greaterThan(a,b) || equalTo(a,b);
    }
}
