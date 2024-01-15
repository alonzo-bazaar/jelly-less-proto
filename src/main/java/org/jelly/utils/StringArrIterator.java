package org.jelly.utils;
import java.util.Iterator;

public class StringArrIterator implements Iterator<String> {
    /**
     * utilizzato per testing
     */
    int indexOfNext = 0;
    String[] ss;

    public StringArrIterator(String[] ss) {
        this.ss = ss;
    }

    @Override
    public boolean hasNext() {
        // has next? == is the next index still valid?
        return indexOfNext < ss.length;
    }

    @Override
    public String next() {
        if(this.hasNext())
            return ss[indexOfNext++];

        return null;
    }
}
