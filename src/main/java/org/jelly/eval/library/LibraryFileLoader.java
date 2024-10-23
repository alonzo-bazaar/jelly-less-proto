package org.jelly.eval.library;

import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.eval.runtime.repl.Printer;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.ArrayUtils;
import org.jelly.utils.ConsUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class LibraryFileLoader {
    public static void loadLibraryFile(ConsList libName, Path cwd, JellyRuntime runtime) throws FileNotFoundException {
        for(Path loadPath : runtime.getLoadPath()) {
            Path loadFilePath = libraryFile(libName, loadPath);
            if(loadFilePath.toFile().exists()) {
                evalPath(runtime, loadFilePath);
                return;
            }
        }

        Path cwdFilePath = libraryFile(libName, cwd);
        if(cwdFilePath.toFile().exists()) {
            evalPath(runtime, cwdFilePath);
            return;
        }

        throw new FileNotFoundException("cannot find file associated with library " + Printer.renderList(libName)
                + "\nno directory in loadPath cotains a file corresponding to: "
                + ArrayUtils.renderArr(ConsUtils.toArray(libName), "/") + ".scm"
                + "\nnor local corresponding file: " + libraryFile(libName, cwd)
                + "\ndo exist");
    }

    private static void evalPath(JellyRuntime runtime, Path path) throws FileNotFoundException {
        File f = new File(path.toString());
        runtime.evalFile(f);
    }

    public static Path libraryFile(ConsList libName, Path startingPath) {
        /* * file convention : se la libreria non è già presente allora si prende un file definito tramite la seguente
         * convenzione
         *
         * 1. tutti gli elementi della lista libraryName osno considerati come subDirectory
         * a partire dalla current working directory, tranne l'ultimo elemento
         * 2. l'ultimo elemento della lista viene considerato come il nome di un file, a cui viene aggiunta l'esetensione
         * '.scm' (scheme)
         *
         * questo processo viene eseguito partendo prima dalla startingPath,
         * e si riprova poi a farlo a partire dalla root della standard
         * library (system dependant)
         *
         * si assume che questo file contenga la libreria richiesta dall'import, questo file viene quindi eseguito
         * e si riprova a fare l'import
         *
         * poi se propio vogliamo esagerare ci sarebbe da controllare che il file in questione contenga effettivamente
         * la libreria col nome specificato e non altra roba ma STICAZZI :)
         */
        Path res = startingPath.toAbsolutePath();
        List<String> strs = ConsUtils.toStream(libName).map(sym -> ((Symbol)sym).name()).toList();

        List<String> dirs = strs.subList(0, strs.size()-1);
        String filename = strs.getLast() + ".scm";

        for(String dir : dirs) {
            res = res.resolve(dir);
        }

        res = res.resolve(filename);

        return res;
    }
}
