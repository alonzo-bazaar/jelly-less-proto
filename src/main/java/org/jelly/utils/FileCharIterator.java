package org.jelly.utils;

import java.util.Iterator;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

public class FileCharIterator implements Iterator<Character> {
    /* wrapper around a FileReader to implement an Iterator<Character> interface
     * it returns a lot more nulls than needed
     * but I don't know how else to handle IO Exceptions
     */

    private FileReader fr;
    Character prefetched = null;
    private boolean eof = false;

    public FileCharIterator(File f) throws FileNotFoundException {
        fr = new FileReader(f);
    }

    @Override
    public Character next() {
        if (this.prefetched != null) {
            Character c = this.prefetched;
            this.prefetched = null;
            return c;
        }
        return maybeNextChar(); // io se mi parte un'eccezoine non so cazzo farti
    }

    @Override
    public boolean hasNext() {
        if (this.eof)
            return false;
        if(this.prefetched != null)
            return true;
        else {
            this.prefetched = maybeNextChar();
            return (prefetched != null);
        }
    }

    private Character maybeNextChar() {
        /* returns the next character if everything goes right
         * null if exception or end of file
         * may set the this.eof flag
         */
        try {
            int x = fr.read();
            if(x == -1) {
                this.eof = true;
                return null;
            }
            return (char)x;
        }
        catch (IOException e) {
            System.out.println("IO Exception : " + e.getMessage());
            return null;
        }
    }
}
