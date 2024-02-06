package org.jelly.eval.evaluable;

import org.jelly.lang.data.Cons;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.utils.Utils;
import org.jetbrains.annotations.NotNull;


public class IfForm implements Form {
    private Evaluable condition;
    private Evaluable ifPart;
    private Evaluable elsePart;

    public IfForm(Evaluable condition,
                  Evaluable ifPart,
                  Evaluable elsePart) {
        this.condition = condition;
        this.ifPart = ifPart;
        this.elsePart = elsePart;
    }

    @NotNull
    public static IfForm fromCheckedAST(Cons c) throws MalformedFormException {
        return new IfForm(EvaluableCreator.fromExpression(c.nth(1)),
                          EvaluableCreator.fromExpression(c.nth(2)),
                          EvaluableCreator.fromExpression(c.nth(3)));
    }

    public static void checkAST(Cons c) throws MalformedFormException {
        FormBuilding.verifyFlatFixed(c, "if", new String[]{"condition", "consequent", "alternative"});
    }

    @Override
    public Object eval(Environment e) {
        if (Utils.isTrue(condition.eval(e))) {
            return ifPart.eval(e);
        }
        return elsePart.eval(e);
    }
}


// Kawa
// primo scheme implementato in jvm
// utilizzato dal bagdanov l'ultima volta nel 2007
