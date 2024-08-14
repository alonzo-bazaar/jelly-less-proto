package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.OrEvaluable;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.utils.ConsUtils;
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
    private static OrEvaluable fromCheckedAST(Cons c) {
        return new OrEvaluable(Utils.toEvaluableList(ConsUtils.requireList(c.getCdr())));
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "or"))
            throw new RuntimeException("ok, or?");
        try {
            Utils.checkSequenceList(ConsUtils.requireList(c.getCdr()));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("or form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("or form is malformed, given parameters are not a list", cce);
        }
    }

}
