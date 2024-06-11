package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.*;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispList;
import org.jelly.utils.LispLists;
import org.jelly.lang.data.LispSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LetFormCompiler implements FormCompiler {
    private final Cons form;
    public LetFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public LetEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    @NotNull
    private static LetEvaluable fromCheckedAST(Cons c) {
        Cons frames = LispLists.requireCons(c.nth(1));
        LispList body = LispLists.requireList(c.nthCdr(2));
        List<LispSymbol> names = ListUtils.toStream(frames)
                .map(LispLists::requireCons)
                .map(bind -> (LispSymbol)bind.nth(0))
                .toList();
        List<Evaluable> vals = ListUtils.toStream(frames)
                .map(LispLists::requireCons)
                .map(bind -> Compiler.compileExpression(bind.nth(1)))
                .toList();
        return new LetEvaluable(names, vals, Utils.sequenceFromConsList(body));
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "let"))
            throw new RuntimeException("let let let");
        try {
            checkBindingsAST(LispLists.requireList(c.nth(1))); // bindings
            Utils.checkSequenceList(LispLists.requireList(c.nthCdr(2)));
        } catch(ClassCastException cce) {
            throw new MalformedFormException("let form is malformed because child element has the wrong type, likely some subform has incorrect nesting, check the parentheses", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("let form is malformed because child is malformed ", mfe);
        }
    }

    private static void checkBindingsAST(LispList ll) throws MalformedFormException {
        List<Object> binds = ListUtils.toJavaList(ll);
        for(Object bind : binds) {
            Cons c = LispLists.requireCons(bind);
            if(c.length() != 2)
                throw new MalformedFormException("let variable binding must have exactly two elements (<var-name> <var-value>)");
            if(!(c.getCar() instanceof LispSymbol))
                throw new MalformedFormException("let variable binding must starts valid variable name (with a symbol), " + c.getCar() + " is not a valid variable name");
            Compiler.checkExpression(c.nth(1));
        }
    }
}
