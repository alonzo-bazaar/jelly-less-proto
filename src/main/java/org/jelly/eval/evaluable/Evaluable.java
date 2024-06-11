package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;

public interface Evaluable {
    public Object eval(Environment e);
}
