package lang;

public class Cons implements LispExpression {
    LispExpression car;
    LispExpression cdr;

    public Cons(LispExpression car, LispExpression cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public LispExpression getCar() {
        return this.car;
    }

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

    public Cons last() {
        Cons c = this;
        for (;
             !Ops.isNil(c) && Ops.isCons(c.getCdr());
             c = (Cons)c.getCdr());
        return c;
    }

    public int length() {
        int res = 1;
        for (Cons c = this;
             !Ops.isNil(c) && Ops.isCons(c.getCdr());
                c = (Cons) c.getCdr()) {
            ++res;
        }
        return res;
    }

    public LispExpression nthCdr(int n) {
        /* 0 indexed, che ci mancherebbe altro */
        if (n <= 0)
            return this;
        if (Ops.isCons(cdr))
            return ((Cons)cdr).nthCdr(n - 1);
        else
            return Constants.NIL;
    }

    public LispExpression nth(int n) {
        /* 0 indexed, che ci mancherebbe altro */
        LispExpression le = nthCdr(n);
        if(Ops.isCons(cdr))
            return ((Cons)le).car;
        return Constants.NIL;
    }
}
