package utils;
/*
 * spero che il codice che sto scrivendo sia leggibile
 * qualora non lo fosse fammi sapere cosa c'è in particolare che non va e ci si lavora
 * se volevi che si presentasse insieme techicamente questo codice l'hai fatto pure te
 * quindi sarebbe gradito si capisse entrambi che cazzo sta facendo
 */

import java.util.Iterator;

public class PrefixIterator implements Iterator<Character> {
    /*
     * the prefix iterator is buffered for ease of checking prefixes
     * we use a circular buffer for convenience
     * (perchè non avevo lo sbatti di capire come fare una cosa del genre con
     * uno dei container predefiniti quindi tiè)
     * 
     * the character stream represented by the iterator is always the concatenation
     * of the characters in the buffer with the stream represented by this.wrapped
     */
    Iterator<Character> wrapped;
    CircularCharBuffer buf = new CircularCharBuffer(this);
    public PrefixIterator(Iterator<Character> wrapped) {
        this.wrapped = wrapped;
    }
    @Override
    public Character next() {
        try {
            if(!buf.isEmpty()) {
                return buf.pop();
            }
        } catch(ArrayStoreException e) {
            System.out.println("e mo' che cazzo doveri fare?");
        }
        return this.wrapped.next();
    }

    @Override
    public boolean hasNext() {
        return (!buf.isEmpty()) || this.wrapped.hasNext();
    }

    public boolean startsWith(String s) {
        buf.pushUntilSize(s.length());
        return buf.isPrefix(s);
    }

    Character _next() {
        return this.wrapped.next();
    }
}

/* 90% di questa roba non sarebbe servita se tu avessi fatto un cazzo di pop_front
 * per i test magari metti prima qualcosa che fa pop_front()
 * poi si sotituisce questo dopo?
 */
class CircularCharBuffer {
    private Character[] buf = new Character[1];
    /* purtroppo ArrayList non permette di ridimensionare l'array a botta
     * quindi si torna al caro vecchissimo
     */
    private PrefixIterator master;

    // start inclusive, end exclusive
    // endIndex==startIndex nel caso (legale) in cui sia 0
    private int startIndex = 0;
    private int endIndex = 0;
    CircularCharBuffer(PrefixIterator master) {
        this.master = master;
    }

    boolean isPrefix(String s) {
        if(s.length() > this.size()) {
            return false;
        }
        int sIter = 0;
        int bIter = this.startIndex;
        while(bIter != this.endIndex &&  sIter != s.length()) {
            if(buf[bIter] != s.charAt(sIter)) {
                return false;
            }
            sIter++;
            bIter = indexAfter(bIter);
        }
        return sIter == s.length();
    }

    void pushUntilSize(int l) {
        if(l > buf.length) {
            increaseBufferSize(l);
        }
        for(int i = 0; i<(l - this.size()); ++i) {
            this.push(master._next());
        }
    }

    boolean isFull() {
        return indexAfter(endIndex) == startIndex;
    }
    boolean isEmpty() {
        return startIndex == endIndex;
    }

    void push(Character c) {
        if (isFull()) {
            moreBufferSize();
        }
        buf[endIndex]=c;
        this.endIndex = indexAfter(this.endIndex);
    }

    Character pop() throws ArrayStoreException{
        if(isEmpty()) {
            throw new ArrayStoreException("trying to pop out of an empty array");
        }
        else {
            Character c = buf[startIndex];
            startIndex = indexAfter(startIndex);
            return c;
        }
    }

    // fine interfaccia, eccovi la ROBA

    void increaseBufferSize(int size) {
        if(size < buf.length) return; // in caso venga chiamata da un cretino

        Character[] newBuf = new Character[size];

        for(int i = 0, j = startIndex; j!=endIndex; j = indexAfter(j)) {
            newBuf[i] = buf[j];
        }

        startIndex = 0;
        endIndex = this.size();
        this.buf = newBuf;
    }

    void moreBufferSize() {
        // euristica alla cazzo di cane
        increaseBufferSize(2*buf.length);
    }


    int size() {
        if(startIndex <= endIndex)
            return endIndex - startIndex;
        else
            return (buf.length - startIndex) + endIndex;
    }

    int indexAfter(int index) {
        return (index + 1) % buf.length;
    }
}
