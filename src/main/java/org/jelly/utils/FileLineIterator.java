package org.jelly.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class FileLineIterator implements Iterator<String> {
    private Scanner scanner;

    public FileLineIterator(File f) throws FileNotFoundException {
        this.scanner = new Scanner(f);
    }
    
    @Override
    public String next() {
        return scanner.nextLine();
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNextLine();
    }
} 
