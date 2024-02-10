package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.AndEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispLists;

public class AndFormCompiler implements FormCompiler {
    private final Cons form;
    public AndFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public AndEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    public static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "and"))
            throw new RuntimeException("ok, and?");
        try {
            Utils.checkSequenceList(LispLists.requireList(c.getCdr()));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("and form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("and form is malformed, given parameters are not a list", cce);
        }
    }

    public static AndEvaluable fromCheckedAST(Cons c) {
        return new AndEvaluable(Utils.toEvaluableList(LispLists.requireList(c.getCdr())));
    }
}
