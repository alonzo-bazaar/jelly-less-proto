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

public class DoFormCompiler implements FormCompiler {
    private final Cons form;
    public DoFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public DoEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    @NotNull
    private static DoEvaluable fromCheckedAST(Cons c) {
        Cons doVars = LispLists.requireCons(c.nth(1));
        Cons doStop = LispLists.requireCons(c.nth(2));
        LispList doBody = LispLists.requireList(c.nthCdr(3));

        List<LispSymbol> names = ListUtils.toStream(doVars)
                .map(var -> (LispSymbol)(((Cons)var).nth(0)))
                .toList();

        List<Evaluable> initForms = ListUtils.toStream(doVars)
                .map(var -> Compiler.compileExpression(((Cons)var).nth(1)))
                .toList();

        List<Evaluable> updateForms = ListUtils.toStream(doVars)
                .map(var -> Compiler.compileExpression(((Cons)var).nth(2)))
                .toList();

        Evaluable stopCondition = Compiler.compileExpression(doStop.nth(0));
        Evaluable returnOnStop = Compiler.compileExpression(doStop.nth(1));

        SequenceEvaluable body = Utils.sequenceFromConsList(doBody);

        return new DoEvaluable(names, initForms, updateForms,
                body,
                stopCondition, returnOnStop);
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "do"))
            throw new RuntimeException("do do do");
        if(c.length() < 3)
            throw new MalformedFormException("do form expects at least two arguments (do (<do-variable>*) <do-stop> <sequence-element>*)");

        try {
            checkVariablesAST(LispLists.requireList(c.nth(1))); // vars
            checkStopAST(LispLists.requireList(c.nth(2))); // stop
            Utils.checkSequenceList(LispLists.requireList(c.nthCdr(3))); // body
        } catch(ClassCastException cce) {
            throw new MalformedFormException("do form is malformed, most likely some subform has incorrect nesting, check the parentheses, the error in most likely somewhere in the variable declarations or in the stop condition\n", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("do form is malformed because child is malformed", mfe);
        }
    }

    private static void checkVariablesAST(LispList ll) throws MalformedFormException {
        List<Object> allVars = ListUtils.toJavaList(ll);
        for(Object o : allVars) {
            List<Object> var = ListUtils.toJavaList(LispLists.requireCons(o));
            if(var.size() != 3)
                throw new MalformedFormException("do variable must have exactly 3 elements, (<name> <init-form> <update-form>)");
            if(!(var.getFirst() instanceof LispSymbol))
                throw new MalformedFormException("do variable binding must starts valid variable name (with a symbol), " + var.get(0) + " is not a valid variable name");
            Compiler.checkExpression(var.get(1));
            Compiler.checkExpression(var.get(2));
        }
    }

    private static void checkStopAST(LispList ll) throws MalformedFormException {
        if(ll.length() > 2) {
            throw new MalformedFormException("do stop must have at most elements ([<stop-condition> [<stop-return>]])");
        }
    }
}
