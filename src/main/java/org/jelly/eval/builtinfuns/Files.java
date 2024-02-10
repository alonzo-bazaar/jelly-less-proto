package org.jelly.eval.builtinfuns;

import java.io.FileNotFoundException;
import java.util.List;
import java.io.File;

import org.jelly.eval.ErrorFormatter;
import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.Constants;

public class Files {
    public static Object loadFile(JellyRuntime r, List<Object> args) {
        Utils.ensureSizeExactly("loadfile", 1, args);
        Utils.ensureSingleOfType("laodfile", 0, String.class, args);
        String filename = (String)args.getFirst();
        try {
            File f = new File((String) args.getFirst());
            return r.evalFile(f);
        } catch(FileNotFoundException ffe) {
            ErrorFormatter.warn("cannot find " + filename + ", no such file or directory");
            return Constants.NIL;
        }
    }

    public static String getWorkingDirectory(List<Object> args) {
        Utils.ensureSizeExactly("loadfile", 0, args);
        return System.getProperty("user.dir");
    }
}
