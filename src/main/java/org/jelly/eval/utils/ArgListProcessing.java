package org.jelly.eval.utils;

import java.util.List;

import org.jelly.lang.data.LispList;
import org.jelly.lang.data.Cons;

public class ArgListProcessing {
    public static Object car(List<Object> args) {
        ArgUtils.ensureSizeExactly("car", 1, args);
        ArgUtils.ensureLispList("car", args.get(0));
        return ((LispList) (args.get(0))).getCar();
    }

    public static Object cdr(List<Object> args) {
        ArgUtils.ensureSizeExactly("cdr", 1, args);
        ArgUtils.ensureLispList("cdr", args.get(0));
        return ((LispList) (args.get(0))).getCdr();
    }

    public static Object cons(List<Object> args) {
        ArgUtils.ensureSizeExactly("cons", 2, args);
        return new Cons(args.get(0), args.get(1));
    }
};
