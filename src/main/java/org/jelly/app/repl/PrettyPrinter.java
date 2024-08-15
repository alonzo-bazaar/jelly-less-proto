package org.jelly.app.repl;

import org.jelly.lang.data.*;

public class PrettyPrinter {
    // per adesso
    public String render(Object o) {
        return switch(o) {
            case ConsList cl -> renderList(cl);
            case Symbol s -> renderSymbol(s);
            default -> o.toString();
        };
    }

    public String renderList(ConsList cl) {
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

    public String renderSymbol(Symbol s) {
        return s.name();
    }
}
