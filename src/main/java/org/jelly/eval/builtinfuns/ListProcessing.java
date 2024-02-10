package org.jelly.eval.builtinfuns;

import java.util.List;

import org.jelly.lang.data.LispList;
import org.jelly.lang.data.Cons;

public class ListProcessing {
    public static Object car(List<Object> args) {
        Utils.ensureSizeExactly("car", 1, args);
        Utils.ensureLispList("car", args.get(0));
        return ((LispList) (args.get(0))).getCar();
    }

    public static Object cdr(List<Object> args) {
        Utils.ensureSizeExactly("cdr", 1, args);
        Utils.ensureLispList("cdr", args.get(0));
        return ((LispList) (args.get(0))).getCdr();
    }

    public static Object cons(List<Object> args) {
        Utils.ensureSizeExactly("cons", 2, args);
        return new Cons(args.get(0), args.get(1));
    }
};
