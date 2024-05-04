package org.jelly.eval.evaluable;

import org.jelly.eval.runtime.Environment;

public interface Evaluable {
    public Object eval(Environment e);
}
