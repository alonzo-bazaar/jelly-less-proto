package org.jelly.utils;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Constants;

import java.security.InvalidParameterException;
import java.util.Iterator;

public class ConsIterator implements Iterator<Object> {
    private ConsList lst;
    private ConsIterator(ConsList lst) {
        this.lst = lst;
    }

    public static ConsIterator from(ConsList lst) {
        if(lst instanceof Cons c && !c.isProperList()) {
            throw new InvalidParameterException("cannot make iterator over improper list " + c);
        }
        return new ConsIterator(lst);
    }

    @Override
    public boolean hasNext() {
        return lst != Constants.NIL;
    }

    @Override
    public Object next() {
        Object res = lst.getCar();
        if(lst.getCdr() instanceof ConsList lstCdr) {
            lst = lstCdr;
        }
        else {
            throw new InvalidParameterException("bro, this was supposed to be unreachable");
        }

        return res;
    }
}
