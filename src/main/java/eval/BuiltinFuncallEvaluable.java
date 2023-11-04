package eval;

import java.security.InvalidParameterException;
import java.util.List;

import java.util.Optional;

import lang.LispSymbol;
import lang.Constants;
import lang.LispList;
import lang.Cons;
import lang.Arith;

public class BuiltinFuncallEvaluable implements Evaluable {
    private final LispSymbol funSym;
    private final List<Evaluable> uncomputed_args;

    public BuiltinFuncallEvaluable(LispSymbol funSym, List<Evaluable> args) {
        this.funSym = funSym;
        this.uncomputed_args = args;
    }

    @Override
    public Object eval(Environment env) {
        return this.call(env);
    }

    private final static String[] builtinFunNames = {
            "cons", "car", "cdr",
            "+", "-", "*", "/", ">", "<", "=", "<=", ">=", "!=",
            "print"
    };
    public static boolean isBuiltinFunctionName(LispSymbol sym) {
        for(String s : builtinFunNames) {
            if (sym.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }


    Object call(Environment env) throws InvalidParameterException {
        String funName = funSym.getName();
        List<Object> computed_args = uncomputed_args.stream().map(exp -> exp.eval(env)).toList();
        switch (funName) {
        case "cons":
            ArgUtils.throwIfNotExactSize("cons", 2, computed_args);
            return new Cons(computed_args.get(0), computed_args.get(1));
        case "car":
            ArgUtils.throwIfNotExactSize("car", 1, computed_args);
            ArgUtils.throwIfNotLispList("car", computed_args.get(0));
            return ((LispList)(computed_args.get(0))).getCar();
        case "cdr":
            ArgUtils.throwIfNotExactSize("cdr", 1, computed_args);
            ArgUtils.throwIfNotLispList("cdr", computed_args.get(0));
            return ((LispList)(computed_args.get(0))).getCdr();
        case "+":
            return ArgArith.sum(computed_args);
        case "-":
            return ArgArith.diff(computed_args);
        case "*":
            return ArgArith.prod(computed_args);
        case "/":
            return ArgArith.ratio(computed_args);
        case ">":
            return ArgArith.greaterThan(computed_args);
        case"<":
            return ArgArith.lessThan(computed_args);
        case "<=":
            return ArgArith.lessEqual(computed_args);
        case ">=":
            return ArgArith.greaterEqual(computed_args);
        case "=":
            return ArgArith.equalTo(computed_args);
        case "!=":
            return ArgArith.notEqualTo(computed_args);

        case "print": // fatto per interagire un po' da ora
            if(computed_args.size() <= 1) {
                for (Object o : computed_args)
                    System.out.println(o + ":" + o.getClass().getSimpleName());
            }
            else {
                System.out.print("[");
                for (Object o : computed_args)
                    System.out.print(o + ":" + o.getClass().getSimpleName() + ", ");
                System.out.println("]");
            }
            return Constants.NIL;
        }
        return null;
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