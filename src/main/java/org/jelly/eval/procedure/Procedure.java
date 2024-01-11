package org.jelly.eval.procedure;

import java.util.List;

public interface Procedure {
    /**
     * @param arguments, the values of the arguments with which the function is called
     */
    public Object call(List<Object> arguments);
    // no env, does not necessarily form a closure
}
