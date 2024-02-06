package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.runtime.EnvFrame;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.utils.Utils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispLists;
import org.jelly.lang.data.LispSymbol;
import org.jetbrains.annotations.NotNull;

public class LetForm implements Form {
    private final List<LispSymbol> names;
    private final List<Evaluable> uncomputedVals;
    private final SequenceEvaluable body;

    public LetForm(List<LispSymbol> names,
                   List<Evaluable> evals,
                   SequenceEvaluable body) {
        this.names = names;
        this.uncomputedVals = evals;
        this.body = body;
    }

    @Override
    public Object eval(Environment e) {
        List<Object> computedVals =
            uncomputedVals
            .stream()
            .map(exp -> exp.eval(e))
            .toList();
        return body.eval(e.extend(new EnvFrame(names, computedVals)));
    }

    @NotNull
    static LetForm fromCheckedAST(Cons c) {
        Cons frames = LispLists.requireCons(c.nth(1));
        LispList body = LispLists.requireList(c.nthCdr(2));
        List<LispSymbol> names = Utils.toStream(frames)
                .map(LispLists::requireCons)
                .map(bind -> (LispSymbol)bind.nth(0))
                .toList();
        List<Evaluable> vals = Utils.toStream(frames)
                .map(LispLists::requireCons)
                .map(bind -> EvaluableCreator.fromExpression(bind.nth(1)))
                .toList();
        return new LetForm(names, vals, FormBuilding.sequenceFromConsList(body));
    }

    static void checkAST(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c, "let"))
            throw new RuntimeException("let let let");
        try {
            checkBindingsAST(LispLists.requireList(c.nth(1))); // bindings
            FormBuilding.ensureSequenceList(LispLists.requireList(c.nthCdr(2)));
        } catch(ClassCastException cce) {
            throw new MalformedFormException("let form is malformed because child element has the wrong type, likely some subform has incorrect nesting, check the parentheses", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("let form is malformed because child is malformed ", mfe);
        }
    }

    private static void checkBindingsAST(LispList ll) throws MalformedFormException {
        List<Object> binds = Utils.toJavaList(ll);
        for(Object bind : binds) {
            Cons c = LispLists.requireCons(bind);
            if(c.length() != 2)
                throw new MalformedFormException("let variable binding must have exactly two elements (<var-name> <var-value>)");
            if(!(c.getCar() instanceof LispSymbol))
                throw new MalformedFormException("let variable binding must starts valid variable name (with a symbol), " + c.getCar() + " is not a valid variable name");
            EvaluableCreator.ensureForm(c.nth(1));
        }
    }
}
