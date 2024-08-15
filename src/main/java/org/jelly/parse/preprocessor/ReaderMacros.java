package org.jelly.parse.preprocessor;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.ConsIterator;
import org.jelly.utils.ListBuilder;

import java.util.Iterator;

public class ReaderMacros implements Iterator<Object> {
    private Iterator<Object> from;
    private ReaderMacros(Iterator<Object> from) {
        this.from = from;
    }
    public static ReaderMacros from(Iterator<Object> from) {
        return new ReaderMacros(from);
    }

    @Override
    public boolean hasNext() {
        return from.hasNext();
    }

    @Override
    public Object next() {
        Object x = from.next();
        return switch (x) {
            case Symbol s -> // a symbol may be a reader macro
                    switch (s.name()) {
                        case "'" -> {
                            if(from.hasNext()) {
                                Object nx = from.next();
                                yield new ListBuilder().addLast(new Symbol("quote")).addLast(deepMacro(nx)).get();
                            }
                            else throw new RuntimeException("quote reader is last token in item stream, what the fuck bro");
                        }
                        case "#f" -> s; // TODO: lambda shorthand
                        // case "#a" -> s;
                        /* array literal va messo altrove,
                         * qui produco codice, se voglio aggiungere sintassi per letterali devo farlo prima
                         * e poi rendere un literalToken
                         */
                        default -> s;
                    };
            case Cons c -> deepMacro(c);
            default -> x; // terminals (not cons)
        };
    }

    private Object deepMacro(Object x) {
        if(x instanceof Cons c) {
            ListBuilder lb = new ListBuilder();
            ConsIterator ci = ConsIterator.from(c);
            ReaderMacros rm = ReaderMacros.from(ci);
            while(rm.hasNext()) {
                lb.addLast(rm.next());
            }
            return lb.get();
        }
        else {
            return x;
        }
    }
}
