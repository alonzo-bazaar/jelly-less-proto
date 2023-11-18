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

