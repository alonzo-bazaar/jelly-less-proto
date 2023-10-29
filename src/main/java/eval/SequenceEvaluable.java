package eval;

import java.util.List;
import lang.LispExpression;

public class SequenceEvaluable implements Evaluable {
    // last is treated differently since evaluation of a sequence returns the value
    // of its last element
    private List<Evaluable> elements;
    private Evaluable last;

    public SequenceEvaluable(List<Evaluable> lst) {
        this.elements = lst.subList(0, lst.size() - 1);
        this.last = lst.get(lst.size() - 1);
    }

    @Override
    public LispExpression eval(Environment e) {
        for (Evaluable ev : elements) {
            ev.eval(e);
        }
        return last.eval(e);
    }
}
