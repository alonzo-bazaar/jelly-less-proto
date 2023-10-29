package eval;

import java.util.List;
import java.util.ArrayList;

import utils.Pair;

import lang.LispExpression;
import lang.Cons;
import lang.Constants;
import lang.LispList;
import lang.Ops;

public class Utils {
    public static Pair<LispList, LispExpression> splitLast(Cons c) {
        return new Pair<>(cutLast(c), c.last());
    }

    static LispList cutLast(LispList c) {
        if (Ops.isNil(c) || Ops.isNil(c.getCdr()))
            return Constants.NIL;
        if (c.getCdr() instanceof LispList ll)
            return new Cons(c.getCar(), cutLast(ll));
        else
            return new Cons(c.getCar(), Constants.NIL);
    }

    public static List<LispExpression> toJavaList(LispList l) {
        ArrayList<LispExpression> al = new ArrayList<>(l.length());
        while (Ops.isCons(l)) {
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
}
