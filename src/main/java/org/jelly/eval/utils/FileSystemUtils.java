package org.jelly.eval.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemUtils {
    public static List<String> pathList(Path path) {
        ArrayList<String> l = new ArrayList<>();
        l.ensureCapacity(path.getNameCount());
        Iterator<Path> pIter = path.toAbsolutePath().iterator();
        while(pIter.hasNext()) {
            l.addLast(pIter.next().toString());
        }
        return l;
    }

    public static Path fileDir(File f) {
        // returns directory in which File f resides, assumed to be the path's parent
        return Paths.get(f.getAbsolutePath()).getParent();
    }
}
