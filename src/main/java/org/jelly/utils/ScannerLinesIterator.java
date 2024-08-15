package org.jelly.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class ScannerLinesIterator implements Iterator<String> {
    private final Scanner scanner;
    private boolean isOpen = false;
    
    public ScannerLinesIterator(Scanner scanner) {
        this.scanner = scanner;
        this.isOpen = true;
    }

    @Override
    public String next() {
        try {
            return scanner.nextLine();
        }
        catch(NoSuchElementException eof) {
            scanner.close();
            System.out.println("\nrecieved end of line, shutting down repl");
            System.out.println("see you soon! :)");
            isOpen = false;
            return "";
        }
    }

    @Override
    public boolean hasNext() {
        return isOpen;
    }
}
