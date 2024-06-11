package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.evaluable.Evaluable;

public interface FormCompiler {
    /* l'interfaccia è fatta con l'idea che la form da controllare/compilare venga passata al FormCompiler alla creazoine
     * (non mi fa specificare il constructor nell'interfaccia, quanto non esprimibile nel codice si esprime nei commenti)
     */
    void check() throws MalformedFormException;
    Evaluable compile();
}
