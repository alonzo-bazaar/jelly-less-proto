package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.evaluable.Evaluable;

public interface FormCompiler {
    /* l'interfaccia è fatta con l'idea che la form da controllare/compilare venga passata al FormCompiler alla creazoine
     * (non mi fa specificare il constructor nell'interfaccia, quanto non esprimibile nel codice si esprime nei commenti)
     *
     * Il check in cascata e il compile in cascata verranno entrambi effettuati creando un albero di form compiler
     * che ricalchi l'albero della sintassi dato dal SyntaxTreeIterator
     * Questo per le form che possono stare nel toplevel
     * per subform quali lambda list, e let binding, o sequenze di espressioni (per un and/sequence/&Co.) non sarà un compiler apposito
     * e ci si ridurrà a usare qualche funzione tirata in una classe di utility
     *
     * credo di avere una mezza fissa per il patter interpreter e famiglia
     *
     * se chiami compile senza aver fatto il check sono cazzi tua
     * il check è separato perchè torna più comodo gestire le eccezioni in questo modo
     */
    void check() throws MalformedFormException;
    Evaluable compile();
}
