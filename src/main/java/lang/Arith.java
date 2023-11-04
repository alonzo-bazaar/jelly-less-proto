package lang;

import org.jetbrains.annotations.NotNull;

public class Arith {
    public static Number add(Number a, Number b) {
        if(a instanceof Integer ai && b instanceof Integer bi)
            return (Integer)(ai + bi);
        else
            return (Double)(a.doubleValue() + b.doubleValue());
    }

    public static Number subtract(Number a, Number b) {
        if(a instanceof Integer ai && b instanceof Integer bi)
            return (Integer)(ai - bi);
        else
            return (Double)(a.doubleValue() - b.doubleValue());
    }

    public static Number multiply(Number a, Number b) {
        if(a instanceof Integer ai && b instanceof Integer bi)
            return (Integer)(ai * bi);
        else
            return (Double)(a.doubleValue() * b.doubleValue());
    }

    public static Number divide(Number a, Number b) {
        return (Double)(a.doubleValue() / b.doubleValue());
    }

    public static boolean lessThan(Number a, Number b) {
        if(a instanceof Integer ai && b instanceof Integer bi)
            return ai < bi;
        else
            return a.doubleValue() < b.doubleValue();
    }

    public static boolean greaterThan(Number a, Number b) {
        if(a instanceof Integer ai && b instanceof Integer bi)
            return ai > bi;
        else
            return a.doubleValue() > b.doubleValue();
    }

    public static boolean equalTo(Number a, Number b) {
        if(a instanceof Integer ai && b instanceof Integer bi)
            return ai.equals(bi);
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
