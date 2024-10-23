package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.library.*;
import org.jelly.eval.runtime.repl.Printer;
import org.jelly.lang.data.ConsList;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class ImportEvaluable implements Evaluable {
    private final LazyImportSet lazy;

    public ImportEvaluable(LazyImportSet lazyImportSet) {
        this.lazy = lazyImportSet;
    }

    @Override
    public Object eval(Environment env) {
        LibraryRegistry registry = env.getLibraryRegistry();

        if(lazy.getLibraryNames().stream().allMatch(registry::hasLibrary)) {
            ImportSet importSet = lazy.get(env.getLibraryRegistry());
            importSet.importInto(env);
            return importSet;
        }

        ImportSet returned = null;
        for(ConsList libname : lazy.getLibraryNames()) {
            returned = tryLoadingLibrary(libname, env);
        }
        return returned;
    }

    private ImportSet tryLoadingLibrary(ConsList libraryName, Environment env) {
        Path cwd = env.getCwd();
        LibraryRegistry registry = env.getLibraryRegistry();

        try {
            LibraryFileLoader.loadLibraryFile(libraryName, cwd, env.getRuntime());

            // una volta caricato il file dove (si pensa) si trovi la libreria
            if(registry.hasLibrary(libraryName)) {
                ImportSet importSet = ImportSet.library(registry.getLibrary(libraryName));
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
                    + Printer.renderList(libraryName)
                    + ", library not defined and library file does not exist",
                    noFile);
        }
    }
}
