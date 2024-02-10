package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.SetEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispSymbol;
import org.jetbrains.annotations.NotNull;

public class SetFormCompiler implements FormCompiler {
    private final Cons form;
    public SetFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public SetEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    @NotNull
    public static SetEvaluable fromCheckedAST(Cons c)  {
        return new SetEvaluable((LispSymbol)c.nth(1), Compiler.compileExpression(c.nth(2)));
    }

    public static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "set!"))
            throw new RuntimeException("set no setty");
        if(c.length() != 3)
            throw new MalformedFormException("set form must have exactly two parameters (set! <var-name> <new-value>)");
        if(!(c.nth(1) instanceof LispSymbol))
            throw new MalformedFormException("set form must with valid variable name (with symbol), " + c.nth(2) + " is not a symbol");
        try {
            Compiler.checkExpression(c.nth(2));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("set form is malformed because assigned value is not a valid form", mfe);
        }
    }
}
