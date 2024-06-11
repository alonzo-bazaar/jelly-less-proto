package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.FuncallEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispList;
import org.jelly.utils.LispLists;
import org.jelly.lang.data.LispSymbol;

public class FuncallFormCompiler implements FormCompiler {
    private final Cons form;
    public FuncallFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public FuncallEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(c.getCar() instanceof LispSymbol) {
            Utils.checkSequenceList(LispLists.requireList(c.getCdr()));
        }
        else {
            // LambdaFormCompiler.checkAST(LispLists.requireCons(c.getCar()));
            Compiler.checkExpression(c.getCar());
            Utils.checkSequenceList(LispLists.requireList(c.getCdr()));
        }
    }

    private static FuncallEvaluable fromCheckedAST(Cons c) {
        return new FuncallEvaluable(Compiler.compileExpression(c.getCar()), ListUtils.toJavaList((LispList)c.getCdr()));
    }
}
