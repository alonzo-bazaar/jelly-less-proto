package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.library.*;
import org.jelly.lang.data.ConsList;

import java.util.List;

public class LibraryDefinitionEvaluable implements Evaluable {
    private final ConsList name;
    private final List<LazyImportSet> importSets;
    private final List<SequenceEvaluable> beginForms;
    private final List<ExportDirective> exportDirectives;

    public LibraryDefinitionEvaluable(ConsList name,
                                      List<LazyImportSet> importSets,
                                      List<SequenceEvaluable> forms,
                                      List<ExportDirective> exportDirectives) {
        this.name = name;
        this.importSets = importSets;
        this.beginForms = forms;
        this.exportDirectives = exportDirectives;
    }

    @Override
    public LazyLibrary eval(Environment env) {
        // Environment ext = new Environment(env.getHead(), env.getTail());
        // Library lib = new Library(ext);
        // for(ImportSet imp : importSets) {
        //     imp.importInto(ext);
        // }

        // ext.push(lib.getBindngsFrame());

        // for(SequenceEvaluable begin : beginForms) {
        //     begin.eval(ext);
        // }

        // for(ExportDirective export : exportDirectives) {
        //     try {
        //         export.directLibrary(lib);
        //     } catch(UnboundVariableException var) {
        //         throw new JellyError("cannot export non-existing variable from library " + Printer.renderList(name), var);
        //     }
        // }
        LazyLibrary ll = new LazyLibrary(env, importSets, beginForms, exportDirectives, name);
        env.getLibraryRegistry().registerLibrary(name, ll);
        return ll;
    }
}
