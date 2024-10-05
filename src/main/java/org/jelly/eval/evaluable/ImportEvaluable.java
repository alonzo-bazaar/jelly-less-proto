package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.evaluable.procedure.Procedure;
import org.jelly.eval.library.*;
import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.eval.runtime.error.JellyError;
import org.jelly.eval.runtime.repl.Printer;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

public class ImportEvaluable implements Evaluable {
    private final LazyImportSet lazy;

    public ImportEvaluable(LazyImportSet lazyImportSet) {
        this.lazy = lazyImportSet;
    }

    @Override
    public Object eval(Environment env) {
        LibraryRegistry registry = env.getLibraryRegistry();

        if(registry.hasLibrary(lazy.getLibraryName())) {
            ImportSet importSet = lazy.get(env.getLibraryRegistry());
            importSet.importInto(env);
            return importSet;
        }

        // se la libreria non è già presente nella registry, si prende il nome della libreria come path relativa
        // e si prova a usarlo come nome di un file che contenga la libreria
        try {
            // leggerissima violenza contro l'astrazione
            ConsList libraryName = lazy.getLibraryName();;
            Path cwd = (Path)((Procedure)env.lookup(new Symbol("getCwdPath"))).apply(List.of());
            JellyRuntime runtime = (JellyRuntime)env.lookup(new Symbol("runtime"));

            LibraryFileLoader.loadLibraryFile(libraryName, cwd, runtime);

            // una volta caricato il file dove (si pensa) si trovi la libreria
            if(registry.hasLibrary(lazy.getLibraryName())) {
                ImportSet importSet = lazy.get(env.getLibraryRegistry());
                importSet.importInto(env);
                return importSet;
            }

            else {
                throw new NoSuchLibraryException("library "
                        + Printer.renderList(libraryName)
                        + " not found in registry, and file " + LibraryFileLoader.libraryFile(libraryName, cwd)
                        + " does not contain the desired library");
            }
        } catch(FileNotFoundException noFile) {
            throw new NoSuchLibraryException("cannot load library "
                    + Printer.renderList(lazy.getLibraryName())
                    + ", library not defined and library file does not exist",
                    noFile);
        }
    }
}
