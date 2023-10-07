package utils;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

public class IteratorCollector {
    /**
     * è abbastanza un functoide
     * mi servivano queste funzioni e non stavano bene da nessun'altra parte
     */

    // che static potesse essere usato per generic non me lo ricordavo
    // https://docs.oracle.com/javase/tutorial/extra/generics/methods.html
    public static <T> LinkedList<T> collectToLinked(Iterator<T> ic) {
        LinkedList<T> res = new LinkedList<T>();
        collectIntoList(ic, res);
        return res;
    }

    public static <T> ArrayList<T> collectToArray(Iterator<T> ic) {
        ArrayList<T> res = new ArrayList<T>();
        collectIntoList(ic, res);
        return res;
    }

    public static String collectToString(Iterator<Character> ic) {
        // dry si fotte un pochino ma non saprei come risolverlo 
        StringBuffer sb = new StringBuffer();
        Character c;
        while(ic.hasNext() && ((c=ic.next()) != null)) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static <T> void collectIntoList(Iterator<T> ic, List<T> li) {
        T t;
        while(ic.hasNext() && ((t=ic.next()) != null)) {
            li.add(t);
        }
    }
}
    
