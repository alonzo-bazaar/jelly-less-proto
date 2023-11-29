package eval;

import lang.Arith;

import java.util.List;
import java.util.Optional;

import eval.errors.IncorrectArgumentsException;

public class ArgArith {
    // "adapts" the Arith functions to act on arg lists
    static Number id(Number n) {
        return n;
    }

    static Number defaultSum(List<Object> args) {
        return args.isEmpty() ? 0 : (Number) args.get(0);
    }

    static Number defaultProduct(List<Object> args) {
        return args.isEmpty() ? 1 : (Number) args.get(0);
    }

    static Number sum(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.throwIfNonNumeric("addition", args);
        Optional<Number> sum = args.stream().map(a -> (Number) a).reduce(Arith::add);
        return sum.map(ArgArith::id).orElse(defaultSum(args));
    }

    static Number diff(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.throwIfNonNumeric("subtraction", args);
        Number zero = defaultSum(args);
        if (args.isEmpty()) return zero;
        Number head = (Number) args.get(0);
        List<Object> tail = args.subList(1, args.size());
        return Arith.subtract(head, sum(tail));
    }

    static Number prod(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.throwIfNonNumeric("multiplication", args);
        Optional<Number> sum = args.stream().map(a -> (Number) a).reduce(Arith::multiply);
        return sum.map(ArgArith::id).orElse(defaultProduct(args));
    }

    static Number ratio(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.throwIfNonNumeric("division", args);
        ArgUtils.throwIfNotExactSize("division", 2, args);
        return Arith.divide((Number) args.get(0), (Number) args.get(1));
    }

    static Integer modulo(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.throwIfNonInteger("modulo", args);
        ArgUtils.throwIfNotExactSize("modulo", 2, args);
        return Arith.modulo((Integer) args.get(0), (Integer) args.get(1));
    }

    // all comparisons perform the same two checks, so why not
    static void comparisonChecks(String source, List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.throwIfNonNumeric(source, args);
        ArgUtils.throwIfNotExactSize(source, 2, args);
    }

    static boolean lessThan(List<Object> args) throws IncorrectArgumentsException { 
        comparisonChecks("'<' comparison", args);
        return Arith.lessThan((Number) args.get(0), (Number) args.get(1));
    }

    static boolean greaterThan(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'>' comparison", args);
        return Arith.greaterThan((Number) args.get(0), (Number) args.get(1));
    }

    static boolean equalTo(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'=' comparison", args);
        return Arith.equalTo((Number) args.get(0), (Number) args.get(1));
    }

    static boolean notEqualTo(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'!=' comparison", args);
        return !Arith.equalTo((Number) args.get(0), (Number) args.get(1));
    }

    static boolean lessEqual(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'<=' comparison", args);
        return Arith.lessEqual((Number) args.get(0), (Number) args.get(1));
    }

    static boolean greaterEqual(List<Object> args) throws IncorrectArgumentsException {
        comparisonChecks("'>=' comparison", args);
        return Arith.greaterEqual((Number) args.get(0), (Number) args.get(1));
    }
}
