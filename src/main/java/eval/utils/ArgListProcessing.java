package eval.utils;

import java.util.List;

import lang.LispList;
import lang.Cons;

public class ArgListProcessing {
    public static Object car(List<Object> args) {
        ArgUtils.throwIfNotExactSize("car", 1, args);
        ArgUtils.throwIfNotLispList("car", args.get(0));
        return ((LispList) (args.get(0))).getCar();
    }

    public static Object cdr(List<Object> args) {
        ArgUtils.throwIfNotExactSize("cdr", 1, args);
        ArgUtils.throwIfNotLispList("cdr", args.get(0));
        return ((LispList) (args.get(0))).getCdr();
    }

    public static Object cons(List<Object> args) {
        ArgUtils.throwIfNotExactSize("cons", 2, args);
        return new Cons(args.get(0), args.get(1));
    }
};
