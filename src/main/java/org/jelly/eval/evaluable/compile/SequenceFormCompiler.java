package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.utils.ConsUtils;
import org.jetbrains.annotations.NotNull;

public class SequenceFormCompiler implements FormCompiler {
    private final Cons form;
    public SequenceFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public SequenceEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    @NotNull
    private static SequenceEvaluable fromCheckedAST(Cons c) {
        return Utils.sequenceFromConsList(ConsUtils.requireList(c.getCdr()));
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "begin"))
            throw new RuntimeException("beelandi amici di striscia!");
        Utils.checkSequenceList(ConsUtils.requireList(c.getCdr()));
    }


}
