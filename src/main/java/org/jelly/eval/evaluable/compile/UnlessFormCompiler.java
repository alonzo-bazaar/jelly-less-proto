package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.ConstantEvaluable;
import org.jelly.eval.evaluable.IfEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;

public class UnlessFormCompiler implements FormCompiler {
    private final Cons form;
    public UnlessFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public IfEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    public IfEvaluable fromCheckedAST(Cons c) {
        return new IfEvaluable(Compiler.compileExpression(c.nth(1)),
                               new ConstantEvaluable(Constants.FALSE),
                               Compiler.compileExpression(c.nth(2)));
    }

    public void checkAST(Cons c) throws MalformedFormException {
        Utils.checkFlatFixed(c, "when", new String[]{"condition", "alternative"});
    }
}
