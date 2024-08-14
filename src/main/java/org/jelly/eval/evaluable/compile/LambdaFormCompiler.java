package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.UserDefinedLambdaEvaluable;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.utils.ConsUtils;
import org.jelly.lang.data.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LambdaFormCompiler implements FormCompiler {
    private final Cons form;
    public LambdaFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public UserDefinedLambdaEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    @NotNull
    private static UserDefinedLambdaEvaluable fromCheckedAST(Cons c) {
        ConsList formalParameters = ConsUtils.requireList(c.nth(1));
        ConsList body = ConsUtils.requireList(c.nthCdr(2));
        List<Symbol> paramsList = ListUtils.toStream(formalParameters)
                .map(a -> (Symbol)a)
                .toList();
        SequenceEvaluable bodEval = Utils.sequenceFromConsList(body);
        return new UserDefinedLambdaEvaluable(paramsList, bodEval);
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c,"lambda"))
            throw new RuntimeException("lambda lambda lambda sosteneva tesi e illusioni");
        if(c.length() < 2)
            throw new MalformedFormException("lambda form must have at least a parameter list");
        try {
            Utils.ensureLambdaListAST(ConsUtils.requireList(c.nth(1)));
            Utils.checkSequenceList(ConsUtils.requireList(c.nthCdr(2)));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("lambda form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("lambda form is malformed, either too short or parameter list is not a list", cce);
        }
    }
}
