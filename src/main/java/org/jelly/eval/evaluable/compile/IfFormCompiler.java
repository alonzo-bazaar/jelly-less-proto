package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.*;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jetbrains.annotations.NotNull;

public class IfFormCompiler implements FormCompiler {
    private final Cons form;
    public IfFormCompiler(Cons form) {
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

    @NotNull
    private static IfEvaluable fromCheckedAST(Cons c) {
        if(c.length() == 3)
            return new IfEvaluable(Compiler.compileExpression(c.nth(1)),
                    Compiler.compileExpression(c.nth(2)),
                    new ConstantEvaluable(Constants.FALSE));
        else // 3 o 4, no exceptions
            return new IfEvaluable(Compiler.compileExpression(c.nth(1)),
                    Compiler.compileExpression(c.nth(2)),
                    Compiler.compileExpression(c.nth(3)));
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(c.length() == 4)
            Utils.checkFlatFixed(c, "if", new String[]{"condition", "consequent", "alternative"});
        else if(c.length() == 3)
            Utils.checkFlatFixed(c, "if", new String[]{"condition", "consequent"});
        else throw new MalformedFormException("if form must have two or three children (if <condition> <consequent>) | (if <condition> <consequent> <alternative>)");
    }
}
