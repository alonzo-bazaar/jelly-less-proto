package lang;

import java.security.InvalidParameterException;

public class Cons implements LispList {
    Object car;
    Object cdr;

    public Cons(Object car, Object cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @Override
    public Object getCar() {
        return this.car;
    }

    @Override
    public Object getCdr() {
        return this.cdr;
    }

    public void setCar(Object newCar) {
        this.car = newCar;
    }

    public void setCdr(Object newCdr) {
        this.cdr = newCdr;
    }

    public void addLast(Object lv) {
        Cons last = this.last();
        last.setCdr(new Cons(lv, Constants.NIL));
    }

    @Override
    public Cons last() {
        Cons c = this;
        while(c.getCdr() instanceof Cons next) {
            c = next;
        }
        return c;
    }

    @Override
    public int length() {
        int cnt = 1;
        Cons c = this;
        while(c.getCdr() instanceof Cons next) {
            c = next;
            ++cnt;
        }
        return cnt;
    }

    @Override
    public Object nthCdr(int n) {
        /* 0 indexed, che ci mancherebbe altro */
        if (n <= 0)
            return this;
        if (cdr instanceof LispList ll)
            return ll.nthCdr(n - 1);
        else
            throw new InvalidParameterException("cannot take cdr of " + cdr.toString() + ". It is not a list");
    }

    @Override
    public Object nth(int n) {
        /* 0 indexed, che ci mancherebbe altro */
        Object cd = nthCdr(n);
        if(cd instanceof Cons cc)
            return cc.car;
        return Constants.NIL;
    }

    @Override
    public String toString() {
        return "cons(" + getCar().toString() + ", " + getCdr().toString() + ")";
    }

    public boolean isProperList() {
        if (cdr == Constants.NIL)
            return true;
        if(cdr instanceof Cons ccdr)
            return ccdr.isProperList();
        return false;
    }
}
