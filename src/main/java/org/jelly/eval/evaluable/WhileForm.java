package org.jelly.eval.evaluable;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.utils.Utils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispLists;

public class WhileForm implements Form {
    private Evaluable cond;
    private SequenceEvaluable body;

    public WhileForm(Evaluable cond, SequenceEvaluable body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public Object eval(Environment e) {
        Object runningValue = Constants.NIL;
        Object check = cond.eval(e);
        while(Utils.isTrue(check)) {
            runningValue = body.eval(e);
            check = cond.eval(e);
        }
        return runningValue;
    }

    public static WhileForm fromCheckedAST(Cons c) {
        return new WhileForm(EvaluableCreator.fromExpression(c.nth(1)),
                             FormBuilding.sequenceFromConsList((LispList) c.nthCdr(2)));
    }

    public static void checkAST(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c, "while"))
            throw new RuntimeException("expected form to start with while symbol, but it starts with " + c.getCar());
        if(c.length() < 2)
            throw new MalformedFormException("while form expected to have exactly at least two members (while <condition> <sequence-element>*");
        try {
            LispList ll = LispLists.requireList(c.getCdr());
            for(Object o : Utils.toJavaList(ll)) {
                EvaluableCreator.ensureForm(o);
            }
        } catch(ClassCastException cce) {
            throw new MalformedFormException("while body is not a list, cannot turn into sequence", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("while form is malformed because body element is malformed", mfe);
        }
    }
}
