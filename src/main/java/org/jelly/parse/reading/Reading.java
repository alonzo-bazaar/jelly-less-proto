package org.jelly.parse.reading;

import org.jelly.parse.preprocessor.ReaderMacros;
import org.jelly.parse.syntaxtree.SyntaxTreeIterator;
import org.jelly.parse.token.TokenIterator;
import org.jelly.utils.FileLineIterator;
import org.jelly.utils.ScannerLinesIterator;
import org.jelly.utils.StringArrIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class Reading {
    public static Iterator<Object> readingFile(File f) throws FileNotFoundException {
        FileLineIterator lines = new FileLineIterator(f);
        if(shebang(f)) {
            lines.next();
        }
        return readingIterator(lines);
    }

    public static Iterator<Object> readingLines(String... lines) {
        return readingIterator(new StringArrIterator(lines));
    }

    public static Iterator<Object> readingScanner(Scanner scanner) {
        return readingIterator(new ScannerLinesIterator(scanner));
    }

    private static Iterator<Object> readingIterator(Iterator<String> lines) {
        return ReaderMacros.from(new SyntaxTreeIterator(new TokenIterator(lines)));
    }

    private static boolean shebang(File f) throws FileNotFoundException {
        String s = new Scanner(f).nextLine();
        return s.startsWith("#!");
    }
}
