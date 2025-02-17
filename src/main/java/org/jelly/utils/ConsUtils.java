package org.jelly.utils;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Nil;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ConsUtils {
    public static Cons requireCons(Object o) {
        if(o instanceof Cons c) {
            return c;
        }
        throw new ClassCastException(o + " required to be cons but is " + o.getClass());
    }

    public static ConsList requireList(Object o) {
        if(o instanceof ConsList ll) {
            return ll;
        }
        throw new ClassCastException(o + " required to be lisp list but is " + o.getClass());
    }

    public static Nil requireNil(Object o) {
        if(o == Constants.NIL) {
            return Constants.NIL;
        }
        throw new ClassCastException(o + " required to be nil but is not");
    }

    public static Object car(Object o) {
        return requireList(o).getCar();
    }

    public static Object cdr(Object o) {
        return requireList(o).getCdr();
    }

    public static Object nth(Object o, int i) {
        return requireList(o).nth(i);
    }

    public static Object nthCdr(Object o, int i) {
        return requireList(o).nthCdr(i);
    }

     public static <T> ConsList toCons(List<T> lst) {
        ListBuilder lb = new ListBuilder();
        for (T o : lst) {
            lb.addLast((Object)o);
        }
        return lb.get();
    }

    public static List<Object> toList(ConsList ll) throws InvalidParameterException {
        if(ll instanceof Cons c && !c.isProperList())
            throw new InvalidParameterException("cannot turn improper cons list into list");

        List<Object> res = new ArrayList<>(ll.length());
        Object d = ll;
        while(d instanceof Cons dl) {
            res.add(dl.getCar());
            d = dl.getCdr();
        }
        return res;
    }

    public static Stream<Object> toStream(ConsList ll) {
        if(ll instanceof Cons c && !c.isProperList())
            throw new InvalidParameterException("cannot turn improper cons list into stream");

        return toList(ll).stream();
    }

    public static Object[] toArray(ConsList ll) throws InvalidParameterException {
        if(ll instanceof Cons c && !c.isProperList())
            throw new InvalidParameterException("cannot turn improper cons list into array");

        Object[] res = new Object[ll.length()];
        int i = 0;

        Object d = ll;
        while(d instanceof Cons dl) {
            res[i] = dl.getCar();
            d = dl.getCdr();
            i++;
        }

        return res;
    }

    public static String renderList(ConsList ll) {
        StringBuilder s = new StringBuilder();
        s.append('(');
        s.append(ArrayUtils.renderArr(toArray(ll), " "));
        s.append(')');
        return s.toString();
    }

    public static ConsList of(Object... elts) {
        ListBuilder lb = new ListBuilder();
        for (Object elt : elts) {
            lb.addLast(elt);
        }
        return lb.get();
    }
}
