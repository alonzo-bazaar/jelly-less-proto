package eval.evaluable;

import java.util.List;

import eval.runtime.Environment;
import eval.utils.Utils;
import lang.Constants;

public class OrEvaluable implements Evaluable {
    private List<Evaluable> elements;

    public OrEvaluable(List<Evaluable> elements) {
        this.elements = elements;
    }

    @Override
    public Object eval(Environment env) {
        for (Evaluable e : elements) {
            Object o = e.eval(env);
            if (!Utils.isFalse(o)) {
                return o;
            }
        }
        return Constants.FALSE;
    }
}
