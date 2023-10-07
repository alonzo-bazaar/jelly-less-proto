package utils;
/*
 * spero che il codice che sto scrivendo sia leggibile
 * qualora non lo fosse fammi sapere cosa c'è in particolare che non va e ci si lavora
 * se volevi che si presentasse insieme techicamente questo codice l'hai fatto pure te
 * quindi sarebbe gradito si capisse entrambi che cazzo sta facendo
 */

import java.util.Iterator;
import java.util.LinkedList;

public class PrefixIterator implements Iterator<Character> {
    /**
     * the prefix iterator is buffered for ease of checking prefixes
     * we use a circular buffer for convenience
     * (perchè non avevo lo sbatti di capire come fare una cosa del genre con
     * uno dei container predefiniti quindi tiè)
     * 
     * the character stream represented by the iterator is always the concatenation
     * of the characters in the buffer with the stream represented by this.wrapped
     */
    Iterator<Character> wrapped;
    CharQueue buf = new LinkedCharBuffer(this);
    public PrefixIterator(Iterator<Character> wrapped) {
        this.wrapped = wrapped;
    }

    public boolean startsWith(String s) {
        buf.pushUntilSize(s.length());
        return buf.isPrefix(s);
    }

    @Override
    public Character next() {
        try {
            if(buf.isEmpty()) {
                return buf.pop();
            }
        } catch(ArrayStoreException e) {
            System.out.println("e mo' che cazzo doveri fare?");
        }
        return this.wrapped.next();
    }

    @Override
    public boolean hasNext() {
        return this._hasNext();
    }

    public String getPrefix(int len) {
        buf.pushUntilSize(len);
        return buf.getPrefix(len);
    }

    public Character peekCharacter() {
        return this.getPrefix(1).charAt(0);
    }

    Character _next() {
        return this.wrapped.next();
    }

    boolean _hasNext() {
        return (buf.isEmpty()) || this.wrapped.hasNext();
    }
}

/**
 * questa non è proprio fatta per essere questa grandissima disaccoppiata
 * è solo che LinkedCharBuffer è inefficiente in culo come implementazione
 * questo è solo per dire "ay fra, sappi che ti levo dal cazzo appena posso"
 * rendendolo il più sostituibile possibile
 */
abstract class CharQueue {
    abstract void pushUntilSize(int size);
    abstract Character pop();
    abstract boolean isPrefix(String s);
    abstract boolean isEmpty();
    abstract String getPrefix(int len);
}

class LinkedCharBuffer extends CharQueue {
    private LinkedList<Character> list = new LinkedList<>();
    private PrefixIterator master;
    LinkedCharBuffer(PrefixIterator master) {
        this.master = master;
    }
    @Override
    void pushUntilSize(int size) {
        while(list.size() <= size) {
            Character next;
            if(master._hasNext() && (next=master._next())!=null) {
                list.addLast(next);
            }
            else break;
        }
    }

    @Override
    Character pop() {
        return list.pop();
    }

    @Override
    boolean isPrefix(String s) {
        if(s.length()>list.size()) {
            return false;
        }
        int i = 0;
        for(Character c:list) {
            if(i==s.length()) break;
            if(s.charAt(i) != c) return false;
            i++;
        }
        return i==s.length();
    }

    @Override
    boolean isEmpty() {
        return !list.isEmpty();
    }

    @Override
    String getPrefix(int len) {
        if (list.size() < len) {
            /* TODO : ok, e che cazzo faccio qui?
             * tiro un'eccezione che poi catcha l'iterator?
             * o cosa?
             * di certo non ritorno null, ma qualcosa devo farla
             */
            System.out.println("serviva un prefisso lungo " + len +
                               " ma ho solo " + list.size() +
                               "(poi grazialcazzo risistemi st'errore, cazzo di print)");
        }
        StringBuffer st = new StringBuffer(len);
        int left = len;
        for (Character c : list) {
            if (left == 0)
                break;
            st.append(c);
            --left;
        }
        return st.toString();
    }
}
