package eval;

import lang.LispList;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class ArgUtils {
    // these checks are so many times I might as well
    static void throwIfNonNumeric(String source, List<Object> args)
            throws InvalidParameterException {
        Optional<Object> guilty = args.stream().filter(a -> !(a instanceof Number)).findFirst();
        if (guilty.isPresent()) {
        throw new InvalidParameterException(source + " expects all arguments to be numeric, argument " +
                guilty.orElse("<bruh what happened here>") +
                " is not numeric");
        }
    }

    static void throwIfNotExactSize(String source, int size, List<Object> args)
            throws InvalidParameterException {
        if (args.size() != size)
            throw new InvalidParameterException(source + " expects exactly " + size + "arguments");
    }

    static void throwIfNotLispList(String source, Object arg) {
        if (!(arg instanceof LispList))
            throw new InvalidParameterException
                    ("cannot call " + source + "on object " + arg
                            + "of type" + arg.getClass().getCanonicalName()
                            + " since it is not a list");
    }

    static void printList(List<Object> args) {
        if (args.size() <= 1) {
            for (Object o : args)
                System.out.println(o + ":" + o.getClass().getSimpleName());
        } else {
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
