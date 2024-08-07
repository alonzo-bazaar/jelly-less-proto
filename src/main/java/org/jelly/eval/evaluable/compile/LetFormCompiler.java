package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.*;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.utils.ConsUtils;
import org.jelly.lang.data.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        if (c.nth(1) instanceof Symbol s) {
            Cons frames = ConsUtils.requireCons(c.nth(2));
            List<Symbol> params = ListUtils.toStream(frames)
                    .map(ConsUtils::requireCons)
                    .map(cell -> (Symbol)cell.nth(0))
                    .toList();
            List<Object> initBinds = ListUtils.toStream(frames)
                    .map(ConsUtils::requireCons)
                    .map(cell -> cell.nth(1))
                    .toList();
            ConsList body = ConsUtils.requireList(c.nthCdr(3));

            UserDefinedLambdaEvaluable fun = new UserDefinedLambdaEvaluable(params, Utils.sequenceFromConsList(body));
            return new LetEvaluable(List.of(), List.of(),
                                    new SequenceEvaluable (List.of(new DefinitionEvaluable(s, fun),
                                                                   new FuncallEvaluable(new LookupEvaluable(s),
                                                                                            initBinds))));
        }
        else {
            Cons frames = ConsUtils.requireCons(c.nth(1));
            ConsList body = ConsUtils.requireList(c.nthCdr(2));
            List<Symbol> names = ListUtils.toStream(frames)
                    .map(ConsUtils::requireCons)
                    .map(bind -> (Symbol) bind.nth(0))
                    .toList();
            List<Evaluable> vals = ListUtils.toStream(frames)
                    .map(ConsUtils::requireCons)
                    .map(bind -> Compiler.compileExpression(bind.nth(1)))
                    .toList();
            return new LetEvaluable(names, vals, Utils.sequenceFromConsList(body));
        }
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "let"))
            throw new RuntimeException("let let let");
        try {
            if(c.nth(1) instanceof Symbol s) {
                checkBindingsAST(ConsUtils.requireList(c.nth(2))); // bindings
                Utils.checkSequenceList(ConsUtils.requireList(c.nthCdr(3)));
            }
            else {
                checkBindingsAST(ConsUtils.requireList(c.nth(1))); // bindings
                Utils.checkSequenceList(ConsUtils.requireList(c.nthCdr(2)));
            }
        } catch(ClassCastException cce) {
            throw new MalformedFormException("let form is malformed because child element has the wrong type, likely some subform has incorrect nesting, check the parentheses", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("let form is malformed because child is malformed ", mfe);
        }
    }

    private static void checkBindingsAST(ConsList ll) throws MalformedFormException {
        List<Object> binds = ListUtils.toJavaList(ll);
        for(Object bind : binds) {
            Cons c = ConsUtils.requireCons(bind);
            if(c.length() != 2)
                throw new MalformedFormException("let variable binding must have exactly two elements (<var-name> <var-value>)");
            if(!(c.getCar() instanceof Symbol))
                throw new MalformedFormException("let variable binding must starts valid variable name (with a symbol), " + c.getCar() + " is not a valid variable name");
            Compiler.checkExpression(c.nth(1));
        }
    }
}
