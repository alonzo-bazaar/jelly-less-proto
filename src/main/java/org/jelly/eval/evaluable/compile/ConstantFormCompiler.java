package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.ConstantEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.Cons;

public class ConstantFormCompiler implements FormCompiler {
    private final Cons form;
    public ConstantFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public ConstantEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    private static ConstantEvaluable fromCheckedAST(Cons c) {
        return new ConstantEvaluable(c.nth(1));
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "quote"))
            throw new RuntimeException("unquote");

        try {
            Compiler.checkExpression(c.nth(1));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("quote form is malformed because the value is malformed", mfe);
        }
    }
}
