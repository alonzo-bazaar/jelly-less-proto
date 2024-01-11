package org.jelly.eval.utils;

import org.jelly.eval.errors.IncorrectArgumentsException;
import org.jelly.lang.javaffi.ForeignMethodCaller;

import java.util.List;

public final class ArgFFI {
    /*
     *  desired lisp api is
     *  (call object methName args)
     *  (callStatic class(name) methName args)
     *
     *  (tryCall object methName args)
     *  (tryCallStatic class(name) methName args)
     *  this is the java code that implements these calls
     */

    /*
     * the call code here is called from the lang.javaffi package
     * so package private methods from other packages are not going to be accessible to the lisp api
     * let us now pretend that this is on purpose
     */
    public static Object call(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureSizeAtLeast("call", 2, args);
        ArgUtils.ensureSingleOfType("call", 1, String.class, args);
        Object[] argsArr = args.subList(2, args.size()).toArray();
        return ForeignMethodCaller.call(args.get(0), (String)args.get(1), argsArr);
    }

    public static Object callStatic(List<Object> args) throws IncorrectArgumentsException {
        ArgUtils.ensureSizeAtLeast("callStatic", 2, args);
        ArgUtils.ensureSingleOfType("callStatic", 0, Class.class, args);
        ArgUtils.ensureSingleOfType("callStatic", 1, String.class, args);
        Object[] argsArr = args.subList(2, args.size()).toArray();
        return ForeignMethodCaller.callStatic((Class<?>)args.get(0), (String)args.get(1), argsArr);
    }
}
