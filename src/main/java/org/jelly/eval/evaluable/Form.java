package org.jelly.eval.evaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;

public interface Form extends Evaluable {
    /* essendo lisp l'ast in questione è un object
     * in quanto l'ast in questione sono dati arbitrari (può anche essere un'atomo)
     */

    static void checkAST(Object o) throws MalformedFormException { }
    static Form fromCheckedAST(Object o) { throw new RuntimeException("this ain't supported"); }
}
