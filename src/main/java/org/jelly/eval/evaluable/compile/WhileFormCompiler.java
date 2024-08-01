package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.WhileEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.utils.ConsUtils;

public class WhileFormCompiler implements FormCompiler {
    private final Cons form;
    public WhileFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public WhileEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }


    private static WhileEvaluable fromCheckedAST(Cons c) {
        return new WhileEvaluable(Compiler.compileExpression(c.nth(1)),
                             Utils.sequenceFromConsList((ConsList) c.nthCdr(2)));
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        if(!Utils.startsWithSym(c, "while"))
            throw new RuntimeException("expected form to start with while symbol, but it starts with " + c.getCar());
        if(c.length() < 2)
            throw new MalformedFormException("while form expected to have exactly at least two members (while <condition> <sequence-element>*");
        try {
            ConsList ll = ConsUtils.requireList(c.getCdr());
            for(Object o : ListUtils.toJavaList(ll)) {
                Compiler.checkExpression(o);
            }
        } catch(ClassCastException cce) {
            throw new MalformedFormException("while body is not a list, cannot turn into sequence", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("while form is malformed because body element is malformed", mfe);
        }
    }
}
