package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.ConstantEvaluable;
import org.jelly.eval.evaluable.IfEvaluable;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;

public class WhenFormCompiler implements FormCompiler {
    private final Cons form;
    public WhenFormCompiler(Cons form) {
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

    private IfEvaluable fromCheckedAST(Cons c) {
        return new IfEvaluable(Compiler.compileExpression(c.nth(1)),
                               Compiler.compileExpression(c.nth(2)),
                               new ConstantEvaluable(Constants.FALSE));
    }

    private void checkAST(Cons c) throws MalformedFormException {
        Utils.checkFlatFixed(c, "when", new String[]{"condition", "consequent"});
    }
}
