package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.CatchForm;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.TryCatchEvaluable;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.AstHandling;
import org.jelly.utils.ConsUtils;

import java.util.Iterator;
import java.util.List;

public class TryCatchFormCompiler implements FormCompiler {
    final private Cons c;
    public TryCatchFormCompiler(Cons c) {
        this.c = c;
    }

    @Override
    public TryCatchEvaluable compile() {
        List<Object> forms = ListUtils.toJavaList(ConsUtils.requireCons(c.getCdr()));
        SequenceEvaluable body = Utils.sequenceFromJavaList(forms.stream().takeWhile(x -> !isCatchForm(x)).toList());

        List<CatchForm> catches = forms.stream()
                .dropWhile(f -> !isCatchForm(f))
                .map(this::compileCatchEvaluable)
                .toList();

        return new TryCatchEvaluable(body, catches);
    }

    @Override
    public void check() throws MalformedFormException {
        if(!Utils.startsWithSym(c, "try"))
            throw new MalformedFormException("bruh");

        Utils.ensureOnlyTailSatisfies((Cons)c.getCdr(), this::isCatchForm);
        Iterator<Object> o = ConsUtils.toStream(c).dropWhile(x -> !isCatchForm(x)).iterator();
        while(o.hasNext()) {
            checkCatchForm(o.next());
        }
    }

    private boolean isCatchForm(Object c) {
        if(c instanceof Cons cc) {
            return AstHandling.requireSymbol(cc.getCar()).name().equals("catch");
        }
        return false;
    }

    private void checkCatchForm (Object c) throws MalformedFormException {
        if(c instanceof Cons cc) {
            if (!Utils.startsWithSym(cc, "catch"))
                throw new MalformedFormException("bruh");

            Cons ccc = ConsUtils.requireCons(cc.nth(1));
            try {
                AstHandling.requireSymbol(ccc.nth(0));
            } catch(ClassCastException cce) {
                throw new MalformedFormException("exception type was supposed to be given as symbol, " + cc.nth(1) + " isn't a symbol", cce);
            }
            try {
                AstHandling.requireSymbol(ccc.nth(1));
            } catch(ClassCastException cce) {
                throw new MalformedFormException("exception name was supposed to be given as symbol, " + cc.nth(1) + " isn't a symbol", cce);
            }

            Utils.checkSequenceList(ConsUtils.requireList(cc.nthCdr(3)));
        }
        else {
            throw new MalformedFormException("catch form ain't even of the right type, form " + c + " is of type " + c.getClass().getCanonicalName());
        }
    }

    private CatchForm compileCatchEvaluable(Object c) {
        Cons cc = ConsUtils.requireCons(c);
        Cons ccc = ConsUtils.requireCons(cc.nth(1));
        Symbol throwableClassName = AstHandling.requireSymbol(ccc.nth(0));
        Symbol throwableName = AstHandling.requireSymbol(ccc.nth(1));
        SequenceEvaluable body = Utils.sequenceFromConsList(ConsUtils.requireCons(cc.nthCdr(2)));
        return new CatchForm(throwableClassName, throwableName, body);
    }
}
