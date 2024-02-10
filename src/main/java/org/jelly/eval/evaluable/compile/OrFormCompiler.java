package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.OrEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispLists;
import org.jetbrains.annotations.NotNull;

public class OrFormCompiler implements FormCompiler {

    @Override
    public OrEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }
    private final Cons form;
    public OrFormCompiler(Cons form) {
        this.form = form;
    }

    @NotNull
    public static OrEvaluable fromCheckedAST(Cons c) {
        return new OrEvaluable(Utils.toEvaluableList(LispLists.requireList(c.getCdr())));
    }

    public static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "or"))
            throw new RuntimeException("ok, or?");
        try {
            Utils.checkSequenceList(LispLists.requireList(c.getCdr()));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("or form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("or form is malformed, given parameters are not a list", cce);
        }
    }

}
