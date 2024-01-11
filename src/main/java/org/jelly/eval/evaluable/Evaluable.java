package org.jelly.eval.evaluable;

import org.jelly.eval.runtime.Environment;

public interface Evaluable {
    public Object eval(Environment e);
    // potrebbe essere un'idea spostare il codice di
    // fromExpression a essere parte di Evaluable,
    // che in EvaluableCreator sta un po' intasando tutto
}
