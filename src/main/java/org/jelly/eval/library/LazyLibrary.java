package org.jelly.eval.library;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.environment.errors.UnboundVariableException;
import org.jelly.eval.evaluable.ImportEvaluable;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.runtime.error.JellyError;
import org.jelly.eval.runtime.repl.Printer;
import org.jelly.lang.data.ConsList;

import java.util.List;

public class LazyLibrary {
    private final List<LazyImportSet> imports;
    private final List<SequenceEvaluable> bodies;
    private final List<ExportDirective> exports;
    private final Environment definitionEnv;

    private final ConsList libraryName; // usato solo per debuggin

    private Library lib = null;

    public LazyLibrary(Environment definitionEnv,
                       List<LazyImportSet> imports,
                       List<SequenceEvaluable> bodies,
                       List<ExportDirective> exports,
                       ConsList libraryName) {
        this.definitionEnv = definitionEnv;
        this.imports = imports;
        this.bodies = bodies;
        this.exports = exports;

        this.libraryName = libraryName;
    }

    public Library get() {
        if(this.lib == null)
            constructLibrary();
        return this.lib;
    }

    private void constructLibrary() {
        Environment ext = new Environment(definitionEnv);
        Library lib = new Library(ext);
        // TODO, brutto, ma anche tanto, sposta il codice di ImportEvaluable da qualche parte dove abbia senso
        for(LazyImportSet imp : imports) {
            ImportEvaluable ugly = new ImportEvaluable(imp);
            ugly.eval(ext);
        }

        ext.push(lib.getBindngsFrame());

        for(SequenceEvaluable begin : bodies) {
            begin.eval(ext);
        }

        for(ExportDirective export : exports) {
            try {
                export.directLibrary(lib);
            } catch(UnboundVariableException var) {
                throw new JellyError("cannot export non-existing variable from library " + Printer.renderList(libraryName), var);
            }
        }

        this.lib = lib;
    }
}
