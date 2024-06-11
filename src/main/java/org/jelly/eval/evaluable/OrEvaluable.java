package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Constants;

public class OrEvaluable implements Evaluable {
    private List<Evaluable> elements;

    public OrEvaluable(List<Evaluable> elements) {
        this.elements = elements;
    }

    @Override
    public Object eval(Environment env) {
        for (Evaluable e : elements) {
            Object o = e.eval(env);
            if (!ListUtils.isFalse(o)) {
                return o;
            }
        }
        return Constants.FALSE;
    }

}
