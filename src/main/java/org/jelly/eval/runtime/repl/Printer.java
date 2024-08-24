package org.jelly.eval.runtime.repl;

import org.jelly.lang.data.*;
import org.jelly.utils.ArrayUtils;

import java.util.List;

public class Printer {
    public static String render(Object o) {
        return switch(o) {
            case ConsList cl -> renderList(cl);
            case Symbol sym -> renderSymbol(sym);
            case String str -> renderString(str);
            case Object[] arr -> renderArray(arr);
            case List<?> l -> renderList(l);
            default -> o.toString();
        };
    }

    public static String renderList(ConsList cl) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        Object i = cl;
        while(i instanceof Cons c) {
            sb.append(render(c.getCar()));

            switch(c.getCdr()) {
                case Cons cd -> sb.append(" "); // list not over
                case Nil n -> {}
                default -> sb.append(" . ").append(render(c.getCdr()));
            };
            i = c.getCdr();
        }
        sb.append(")");
        return sb.toString();
    }

    public static String renderSymbol(Symbol s) {
        return s.name();
    }

    public static String renderArray(Object[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i<arr.length - 1; ++i) {
            sb.append(arr[i]).append(", ");
        }
        if(arr.length != 0)
            sb.append(arr[arr.length-1]);
        sb.append("]");
        return sb.toString();
    }

    public static <T> String renderList(List<T> l) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        for(int i = 0; i<l.size() - 1; ++i) {
            sb.append(l.get(i)).append(", ");
        }
        if(!l.isEmpty())
            sb.append(l.getLast());
        sb.append(">");
        return sb.toString();
    }

    public static String renderString(String s) {
        return "\"" + s + "\"";
    }
}
