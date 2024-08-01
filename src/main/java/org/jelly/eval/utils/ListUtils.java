package org.jelly.eval.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.jelly.utils.Pair;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.ConsList;

public class ListUtils {
    public static Pair<ConsList, Object> splitLast(Cons c) {
        return new Pair<>(cutLast(c), c.last());
    }

    static ConsList cutLast(ConsList c) {
        if (c == Constants.NIL || c.getCdr() == Constants.NIL)
            return Constants.NIL;
        if (c.getCdr() instanceof ConsList ll)
            return new Cons(c.getCar(), cutLast(ll));
        else
            return new Cons(c.getCar(), Constants.NIL);
    }

    public static List<Object> toJavaList(ConsList l) {
        ArrayList<Object> al = new ArrayList<>(l.length());
        while (l instanceof Cons) {
            al.add(l.getCar());
            if (l.getCdr() instanceof Cons c) {
                l = c;
            }
            else {
                break;
            }
        }
        return al;
    }

    public static Stream<Object> toStream(ConsList l) {
        return toJavaList(l).stream();
    }

    public static boolean isFalse(Object o) {
        return o == Boolean.FALSE;
    }

    public static boolean isTrue(Object o) {
        return !isFalse(o);
    }
}

