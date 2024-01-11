package org.jelly.eval.evaluable;

import org.jelly.eval.runtime.Environment;
import org.jelly.eval.utils.Utils;

public class IfEvaluable implements Evaluable {
    private Evaluable condition;
    private Evaluable ifPart;
    private Evaluable elsePart;

    public IfEvaluable(Evaluable condition,
                       Evaluable ifPart,
                       Evaluable elsePart) {
        this.condition = condition;
        this.ifPart = ifPart;
        this.elsePart = elsePart;
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
