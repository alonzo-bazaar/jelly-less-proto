package lang;

import java.security.InvalidParameterException;

public class Cons implements LispList {
    LispExpression car;
    LispExpression cdr;

    public Cons(LispExpression car, LispExpression cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    @Override
    public LispExpression getCar() {
        return this.car;
    }

    @Override
    public LispExpression getCdr() {
        return this.cdr;
    }

    public void setCar(LispExpression newCar) {
        this.car = newCar;
    }

    public void setCdr(LispExpression newCdr) {
        this.cdr = newCdr;
    }

    public void addLast(LispExpression lv) {
        Cons last = this.last();
        last.setCdr(new Cons(lv, Constants.NIL));
    }

    @Override
    public Cons last() {
        Cons c;
        for (c = this;
             !Ops.isNil(c) && c.getCdr() instanceof Cons next;
             c = next);
        return c;
    }

    @Override
    public int length() {
        int res = 1;
        for (Cons c = this;
             !Ops.isNil(c) && Ops.isCons(c.getCdr());
                c = (Cons) c.getCdr()) {
            ++res;
        }
        return res;
    }

    @Override
    public LispExpression nthCdr(int n) {
        /* 0 indexed, che ci mancherebbe altro */
        if (n <= 0)
            return this;
        if (cdr instanceof LispList ll)
            return ll.nthCdr(n - 1);
        else
            throw new InvalidParameterException("cannot take cdr of " + cdr.toString() + ". It is not a list");
    }

    @Override
    public LispExpression nth(int n) {
        /* 0 indexed, che ci mancherebbe altro */
        LispExpression le = nthCdr(n);
        if(Ops.isCons(cdr))
            return ((Cons)le).car;
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
