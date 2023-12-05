package eval.evaluable;

import java.util.List;
import java.util.ArrayList;

import eval.runtime.Environment;
import lang.Constants;

public class SequenceEvaluable implements Evaluable {
    // last is treated differently since evaluation of a sequence returns the value
    // of its last element
    private List<Evaluable> elements;
    private Evaluable last;

    public SequenceEvaluable(List<Evaluable> lst) {
        if (lst.isEmpty()) {
            this.last = new ConstantEvaluable(Constants.UNDEFINED);
            this.elements = new ArrayList<Evaluable>(0);
        }
        else {
            this.elements = lst.subList(0, lst.size() - 1);
            this.last = lst.get(lst.size() - 1);
        }
    }

    @Override
    public Object eval(Environment e) {
        for (Evaluable ev : elements) {
            ev.eval(e);
        }
        return last.eval(e);
    }
}
