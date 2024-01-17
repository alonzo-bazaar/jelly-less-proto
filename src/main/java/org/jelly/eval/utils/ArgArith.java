package org.jelly.eval.utils;

import org.jelly.lang.Arith;

import java.util.List;
import java.util.Optional;

import org.jelly.eval.errors.IncorrectArgumentsException;

public class ArgArith {
    // "adapts" the Arith functions to act on arg lists
    public static Number id(Number n) {
        return n;
    }

    public static Number defaultSum(List<Object> args) {
        return args.isEmpty() ? 0 : (Number) args.get(0);
    }

    public static Number defaultProduct(List<Object> args) {
        return args.isEmpty() ? 1 : (Number) args.get(0);
    }

    public static Number sum(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureAllNumeric("addition", args);
        Optional<Number> sum = args.stream().map(a -> (Number) a).reduce(Arith::add);
        return sum.map(ArgArith::id).orElse(defaultSum(args));
    }

    public static Number diff(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureAllNumeric("subtraction", args);
        Number zero = defaultSum(args);
        if (args.isEmpty()) return zero;
        Number head = (Number) args.get(0);
        List<Object> tail = args.subList(1, args.size());
        return Arith.subtract(head, sum(tail));
    }

    public static Number prod(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureAllNumeric("multiplication", args);
        Optional<Number> sum = args.stream().map(a -> (Number) a).reduce(Arith::multiply);
        return sum.map(ArgArith::id).orElse(defaultProduct(args));
    }

    public static Number ratio(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureAllNumeric("division", args);
        ArgUtils.ensureSizeExactly("division", 2, args);
        return Arith.divide((Number) args.get(0), (Number) args.get(1));
    }

    public static Integer modulo(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureAllIntegers("modulo", args);
        ArgUtils.ensureSizeExactly("modulo", 2, args);
        return Arith.modulo((Number)args.get(0), (Number)args.get(1));
    }

    // all comparisons perform the same two checks, so why not
    public static void comparisonChecks(String source, List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureAllNumeric(source, args);
        ArgUtils.ensureSizeExactly(source, 2, args);
    }

    public static boolean lessThan(List<Object> args) throws IncorrectArgumentsException { 
        comparisonChecks("'<' comparison", args);
        return Arith.lessThan((Number) args.get(0), (Number) args.get(1));
    }

    public static boolean greaterThan(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'>' comparison", args);
        return Arith.greaterThan((Number) args.get(0), (Number) args.get(1));
    }

    public static boolean equalTo(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'=' comparison", args);
        return Arith.equalTo((Number) args.get(0), (Number) args.get(1));
    }

    public static boolean notEqualTo(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'!=' comparison", args);
        return !Arith.equalTo((Number) args.get(0), (Number) args.get(1));
    }

    public static boolean lessEqual(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'<=' comparison", args);
        return Arith.lessEqual((Number) args.get(0), (Number) args.get(1));
    }

    public static boolean greaterEqual(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'>=' comparison", args);
        return Arith.greaterEqual((Number) args.get(0), (Number) args.get(1));
    }
}
