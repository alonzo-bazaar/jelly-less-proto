package org.jelly.eval.evaluable;

import java.util.List;
import java.util.ArrayList;

import org.jelly.eval.runtime.Environment;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Constants;

public class AndEvaluable implements Evaluable {
    private List<Evaluable> elements;
    private Evaluable last;

    public AndEvaluable(List<Evaluable> lst) {
        if (lst.isEmpty()) {
            this.elements = new ArrayList<Evaluable>();
            this.last = new ConstantEvaluable(Constants.TRUE);
        }
        else {
            this.elements = lst.subList(0, lst.size() - 1);
            this.last = lst.getLast();
        }
    }

    @Override
    public Object eval(Environment env) {
        for (Evaluable e : elements) {
            Object o = e.eval(env);
            if (ListUtils.isFalse(o)) {
                return Constants.FALSE;
            }
        }
        return last.eval(env);
    }
}
