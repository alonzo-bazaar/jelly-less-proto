package org.jelly.eval.evaluable.procedure;

import java.util.List;

public interface Procedure {
    /**
     * @param arguments, the values of the arguments with which the function is called
     */
    public Object apply(List<Object> arguments);
    // no env, does not necessarily form a closure
}
