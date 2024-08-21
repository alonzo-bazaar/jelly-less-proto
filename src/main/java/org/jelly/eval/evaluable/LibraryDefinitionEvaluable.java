package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.library.ExportDirective;
import org.jelly.eval.library.ImportSet;
import org.jelly.eval.library.Library;
import org.jelly.eval.library.Registry;
import org.jelly.lang.data.ConsList;

import java.util.List;

public class LibraryDefinitionEvaluable implements Evaluable {
    private final ConsList name;
    private final List<ImportSet> importSets;
    private final List<SequenceEvaluable> beginForms;
    private final List<ExportDirective> exportDirectives;

    public LibraryDefinitionEvaluable(ConsList name,
                                      List<ImportSet> importSets,
                                      List<SequenceEvaluable> forms,
                                      List<ExportDirective> exportDirectives) {
        this.name = name;
        this.importSets = importSets;
        this.beginForms = forms;
        this.exportDirectives = exportDirectives;
    }

    @Override
    public Library eval(Environment env) {
        Library lib = new Library();
        Environment ext = new Environment(env.getHead(), env.getTail());
        for(ImportSet imp : importSets) {
            imp.importInto(ext);
        }

        ext.push(lib.getInternalEnv());

        for(SequenceEvaluable begin : beginForms) {
            begin.eval(ext);
        }

        for(ExportDirective export : exportDirectives) {
            export.directLibrary(lib);
        }

        Registry.registerLibrary(name, lib);
        return lib;
    }
}
