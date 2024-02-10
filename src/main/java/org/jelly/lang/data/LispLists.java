package org.jelly.lang.data;

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
}
