package org.jelly.utils;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.NilValue;

import java.util.List;
import java.util.ArrayList;

public class LispLists {
    public static Cons requireCons(Object o) {
        if(o instanceof Cons c) {
            return c;
        }
        throw new ClassCastException(o + " required to be cons but is " + o.getClass());
    }

    public static LispList requireList(Object o) {
        if(o instanceof LispList ll) {
            return ll;
        }
        throw new ClassCastException(o + " required to be lisp list but is " + o.getClass());
    }

    public static NilValue requireNil(Object o) {
        if(o == Constants.NIL) {
            return Constants.NIL;
        }
        throw new ClassCastException(o + " required to be nil but is not");
    }

    public static Object nth(Object o, int i) {
        return requireList(o).nth(i);
    }

    public static Object nthCdr(Object o, int i) {
        return requireList(o).nthCdr(i);
    }

     public static <T> LispList javaListToCons(List<T> lst) {
        ListBuilder lb = new ListBuilder();
        for (T o : lst) {
            lb.addLast((Object)o);
        }
        return lb.get();
    }

    public static List<Object> lispListToJava(LispList ll) {
        List<Object> res = new ArrayList<>();
        Object d = ll;
        while(d instanceof Cons dl) {
            res.add(dl.getCar());
            d = dl.getCdr();
        }
        return res;
    }
}
