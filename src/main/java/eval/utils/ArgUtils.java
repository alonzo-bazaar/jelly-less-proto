package eval.utils;

import eval.evaluable.EvaluableCreator;
import lang.LispList;
import lang.ListBuilder;

import java.util.List;
import java.util.Optional;

import eval.errors.IncorrectArgumentListException;
import eval.errors.IncorrectTypeException;

import eval.runtime.Environment;

public class ArgUtils {
    // these checks are done so many times I might as well
    public static void throwIfNonNumeric(String source, List<Object> args)
            throws IncorrectTypeException {
        Optional<Object> guilty = args.stream().filter(a -> !(a instanceof Number)).findFirst();
        if (guilty.isPresent()) {
        throw new IncorrectTypeException(source + " expects all arguments to be numeric, argument " +
                guilty.orElse("<bruh what happened here>") +
                " is not numeric");
        }
    }

    public static void throwIfNonInteger(String source, List<Object> args)
            throws IncorrectTypeException {
        Optional<Object> guilty = args.stream().filter(a -> !(a instanceof Integer)).findFirst();
        if (guilty.isPresent()) {
        throw new IncorrectTypeException(source + " expects all arguments to be integers, argument " +
                guilty.orElse("<bruh what happened here>") +
                " is not an integer");
        }
    }

    public static void throwIfNotExactSize(String source, int size, List<Object> args)
            throws IncorrectArgumentListException {
        if (args.size() != size)
            throw new IncorrectArgumentListException(source + " expects exactly " + size + "arguments");
    }

    public static void throwIfNotLispList(String source, Object arg) throws IncorrectTypeException {
        if (!(arg instanceof LispList))
            throw new IncorrectTypeException
                    ("cannot call " + source + "on object " + arg
                            + "of type" + arg.getClass().getCanonicalName()
                            + " since it is not a list");
    }

    public static void printListWithTypes(List<Object> args, String separator) {
        if (args.size() <= 1) {
            for (Object o : args)
                System.out.print(o + ":" + o.getClass().getSimpleName());
        } else {
            for (Object o : args)
                System.out.print(o + ":" + o.getClass().getSimpleName() + separator);
        }
    }

    public static void printList(List<Object> args, String separator) {
        if (args.size() <= 1) {
            for (Object o : args)
                System.out.print(o);
        } else {
            for (Object o : args)
                System.out.print(o + separator);
        }
    }

    public static List<Object> evlist(List<Object> lst, Environment env) {
        return lst
                .stream()
                .map(o -> EvaluableCreator.fromExpression(o).eval(env))
                .toList();
    }

    public static LispList javaListToCons(List<Object> lst) {
        ListBuilder lb = new ListBuilder();
        for (Object o : lst) {
            lb.addLast(o);
        }
        return lb.get();
    }
}
