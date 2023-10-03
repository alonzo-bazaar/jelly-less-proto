package utils;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

public class IteratorCollector {
    /**
     * abbastanza un functoide */

    public static LinkedList<Object> collectToLinked(Iterator<Object> ic) {
        LinkedList<Object> res = new LinkedList<Object>();
        collectIntoList(ic, res);
        return res;
    }

    public static ArrayList<Object> collectToArray(Iterator<Object> ic) {
        ArrayList<Object> res = new ArrayList<Object>();
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

    private static void collectIntoList(Iterator<Object> ic, List<Object> li) {
        Object o;
        while(ic.hasNext() && ((o=ic.next()) != null)) {
            li.add(o);
        }
    }
}
    
