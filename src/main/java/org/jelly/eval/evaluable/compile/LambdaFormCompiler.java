package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.UserDefinedLambdaEvaluable;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispLists;
import org.jelly.lang.data.LispSymbol;
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
    public static UserDefinedLambdaEvaluable fromCheckedAST(Cons c) {
        LispList formalParameters = LispLists.requireList(c.nth(1));
        LispList body = LispLists.requireList(c.nthCdr(2));
        List<LispSymbol> paramsList = ListUtils.toStream(formalParameters)
                .map(a -> (LispSymbol)a)
                .toList();
        SequenceEvaluable bodEval = Utils.sequenceFromConsList(body);
        return new UserDefinedLambdaEvaluable(paramsList, bodEval);
    }

    public static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c,"lambda"))
            throw new RuntimeException("lambda lambda lambda sosteneva tesi e illusioni");
        if(c.length() < 2)
            throw new MalformedFormException("lambda form must have at least a parameter list");
        try {
            ensureLambdaListAST(LispLists.requireList(c.nth(1)));
            Utils.checkSequenceList(LispLists.requireList(c.nthCdr(2)));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("lambda form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("lambda form is malformed, either too short or parameter list is not a list", cce);
        }
    }

    public static void ensureLambdaListAST(LispList ll) throws MalformedFormException {
        List<Object> l = ListUtils.toJavaList(ll);
        for(Object o : l) {
            if(!(o instanceof LispSymbol)) {
                throw new MalformedFormException(o + " should not be in a lambda list, only symbols can be there");
            }
        }
    }
}
