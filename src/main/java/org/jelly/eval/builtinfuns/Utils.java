package org.jelly.eval.builtinfuns;

import org.jelly.eval.evaluable.compile.Compiler;
import org.jelly.lang.data.ConsList;

import java.util.List;
import java.util.Optional;

import org.jelly.eval.errors.IncorrectArgumentListException;
import org.jelly.eval.errors.IncorrectTypeException;

import org.jelly.eval.environment.Environment;

public class Utils {
    // these checks are done so many times I might as well
    public static void ensureAllNumeric(String source, List<Object> args)
            throws IncorrectTypeException {
        Optional<Object> guilty = args.stream().filter(a -> !(a instanceof Number)).findFirst();
        if (guilty.isPresent()) {
        throw new IncorrectTypeException("<" + source + "> expects all arguments to be numeric, argument <" +
                guilty.orElse("<bruh what happened here>") +
                "> is not numeric");
        }
    }

    public static void ensureAllIntegers(String source, List<Object> args)
            throws IncorrectTypeException {
        Optional<Object> guilty = args.stream().filter(a -> !(a instanceof Integer)).findFirst();
        if (guilty.isPresent()) {
        throw new IncorrectTypeException("<" + source + "> expects all arguments to be integers, argument <" +
                guilty.orElse("<bruh what happened here>") +
                "> is not an integer");
        }
    }

    public static void ensureSingleOfType(String source, int position, Class<?> cls, List<Object> args) {
        Object obj = args.get(position);
        if(!cls.isInstance(obj))
            throw new IncorrectTypeException("<" + source + "> expects its #[" + (position + 1) +
                    "] th argument to be of type <" + cls.getCanonicalName() +
                    ">, but " + obj + " was given, of type <" + obj.getClass().getCanonicalName() + ">");
    }

    public static void ensureSizeAtLeast(String source, int minSize, List<Object> args) {
        if(args.size() < minSize) {
            throw new IncorrectArgumentListException("<" + source + "> expects at least <" + minSize +
                                                     "> arguments, but <" + args.size() + "> arguments were provided instead");
        }
    }

    public static void ensureSizeExactly(String source, int size, List<Object> args)
            throws IncorrectArgumentListException {
        if (args.size() != size)
            throw new IncorrectArgumentListException("<" + source + "> expects exactly <" + size + "> arguments");
    }

    public static void ensureLispList(String source, Object arg) throws IncorrectTypeException {
        if (!(arg instanceof ConsList))
            throw new IncorrectTypeException
                    ("cannot call <" + source + "> on object " + arg
                            + "of type <" + arg.getClass().getCanonicalName()
                            + "> since it is not a list");
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
                .map(o -> Compiler.compileExpression(o).eval(env))
                .toList();
    }
}
