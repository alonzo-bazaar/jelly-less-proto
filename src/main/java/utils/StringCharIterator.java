package utils;

import java.util.Iterator;

public class StringCharIterator implements Iterator<Character> {
    String s;
    int index = 0;

    public StringCharIterator(String s) {
        this.s = s;
    }

    @Override
    public Character next() {
        if (!this.hasNext()) {
            return null;
        }
        Character c = s.charAt(index);
        ++index;
        return c;
    }
    
    @Override
    public boolean hasNext() {
        return (index < s.length());
    }
    
}
