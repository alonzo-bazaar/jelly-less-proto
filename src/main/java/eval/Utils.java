package eval;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import lang.Arith;
import utils.Pair;

import lang.Cons;
import lang.Constants;
import lang.LispList;

public class Utils {
    public static Pair<LispList, Object> splitLast(Cons c) {
        return new Pair<>(cutLast(c), c.last());
    }

    static LispList cutLast(LispList c) {
        if (c == Constants.NIL || c.getCdr() == Constants.NIL)
            return Constants.NIL;
        if (c.getCdr() instanceof LispList ll)
            return new Cons(c.getCar(), cutLast(ll));
        else
            return new Cons(c.getCar(), Constants.NIL);
    }

    public static List<Object> toJavaList(LispList l) {
        ArrayList<Object> al = new ArrayList<>(l.length());
        while (l instanceof Cons) {
            al.addLast(l.getCar());
            if (l.getCdr() instanceof Cons c) {
                l = c;
            }
            else {
                break;
            }
        }
        return al;
    }

    public static boolean isFalsey(Object o) {
        return o == Constants.NIL || o == Boolean.FALSE;
    }

    public static boolean isTruthy(Object o) {
        return !isFalsey(o);
    }

}

class ArgUtils {
    // these checks are so many times I might as well
    static void throwIfNonNumeric(String source, List<Object> args)
            throws InvalidParameterException {
        if(!args.stream().allMatch(a -> a instanceof Number))
            throw new InvalidParameterException(source + " expects all arguments to be numeric");
    }

    static void throwIfNotExactSize(String source, int size, List<Object> args)
            throws InvalidParameterException {
        if(!args.stream().allMatch(a -> a instanceof Number))
            throw new InvalidParameterException(source + " expects exactly " + size + "arguments");
    }

    static void throwIfNotLispList(String source, Object arg) {
        if(!(arg instanceof LispList))
            throw new InvalidParameterException
                    ("cannot call " + source + "on object " + arg
                            + "of type" + arg.getClass().getCanonicalName()
                            + " since it is not a list");
    }

    static void printList(List<Object> args) {
        if(args.size() <= 1) {
            for (Object o : args)
                System.out.println(o + ":" + o.getClass().getSimpleName());
        }
        else {
            System.out.print("[");
            for (Object o : args)
                System.out.print(o + ":" + o.getClass().getSimpleName() + ", ");
            System.out.println("]");
        }
    }

    static List<Object> evlist(List<Object> lst, Environment env) {
        return lst
                .stream()
                .map(o -> EvaluableCreator.fromExpression(o).eval(env))
                .toList();
    }
}

class ArgArith {
    // "adapts" the Arith functions to act on arg lists
    static Number id(Number n) {
        return n;
    }
    static Number defaultSum(List<Object> args) {
        return args.isEmpty()?0:(Number)args.get(0);
    }
    static Number defaultProduct(List<Object> args) {
        return args.isEmpty()?1:(Number)args.get(0);
    }
    static Number sum(List<Object> args) {
        ArgUtils.throwIfNonNumeric("addition", args);
        Optional<Number> sum = args.stream().map(a -> (Number)a).reduce(Arith::add);
        return sum.map(ArgArith::id).orElse(defaultSum(args));
    }
    static Number diff(List<Object> args) {
        ArgUtils.throwIfNonNumeric("subtraction", args);
        Number zero = defaultSum(args);
        if(args.isEmpty()) return zero;
        Number head = (Number)args.get(0);
        List<Object> tail = args.subList(1,args.size());
        return Arith.subtract(head,sum(tail));
    }
    static Number prod(List<Object> args) {
        ArgUtils.throwIfNonNumeric("multiplication", args);
        Optional<Number> sum = args.stream().map(a -> (Number)a).reduce(Arith::multiply);
        return sum.map(ArgArith::id).orElse(defaultProduct(args));
    }
    static Number ratio(List<Object> args) {
        ArgUtils.throwIfNonNumeric("division", args);
        ArgUtils.throwIfNotExactSize("division", 2, args);
        return Arith.divide((Number)args.get(0), (Number)args.get(1));
    }

    // all comparisons perform the same two checks, so why not
    static void comparisonChecks(String source, List<Object> args) {
        ArgUtils.throwIfNonNumeric(source, args);
        ArgUtils.throwIfNotExactSize(source, 2, args);
    }

    static boolean lessThan(List<Object> args) {
        comparisonChecks("'<' comparison", args);
        return Arith.lessThan((Number)args.get(0), (Number)args.get(1));
    }

    static boolean greaterThan(List<Object> args) {
        comparisonChecks("'>' comparison", args);
        return Arith.greaterThan((Number)args.get(0), (Number)args.get(1));
    }

    static boolean equalTo(List<Object> args) {
        comparisonChecks("'=' comparison", args);
        return Arith.equalTo((Number)args.get(0), (Number)args.get(1));
    }

    static boolean notEqualTo(List<Object> args) {
        comparisonChecks("'!=' comparison", args);
        return !Arith.equalTo((Number)args.get(0), (Number)args.get(1));
    }

    static boolean lessEqual(List<Object> args) {
        comparisonChecks("'<=' comparison", args);
        return Arith.lessEqual((Number)args.get(0), (Number)args.get(1));
    }

    static boolean greaterEqual(List<Object> args) {
        comparisonChecks("'>=' comparison", args);
        return Arith.lessEqual((Number)args.get(0), (Number)args.get(1));
    }
}
