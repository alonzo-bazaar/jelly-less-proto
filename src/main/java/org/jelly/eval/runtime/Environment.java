package org.jelly.eval.runtime;

import java.util.List;

import org.jelly.eval.ErrorFormatter;
import org.jelly.eval.runtime.errors.VariableDoesNotExistException;
import org.jelly.lang.data.LispSymbol;

public class Environment {
    // definizione abbastanza poco implicita come lista concatenata
    // terminata da null perchè sì
    private EnvFrame head;
    private Environment tail = null;

    public Environment() {
        this.head = new EnvFrame();
        this.tail = null;
    }

    public Environment(EnvFrame frame) {
        this.head = frame;
        this.tail = null;
    }

    public Environment(EnvFrame head, Environment tail) {
        this.head = head;
        this.tail = tail;
    }

    public Object lookup(LispSymbol name) {
        // mi spiace per il null ma non vedo come altro esprimere
        // "hai cercato un valore che non esisteva"
        // non ha troppo senso tirare un'eccezione qui, è un comportamento previsto(?)
        Object h = head.lookup(name);
        if (h == null) {
            if (tail == null)
                return null;
            return tail.lookup(name);
        }
        return h;
    }

    public Environment extend() {
        return new Environment(new EnvFrame(), this);
    }

    public Environment extend(EnvFrame frame) {
        return new Environment(frame, this);
    }

    public Environment extend(List<LispSymbol> ls, List<Object> le) {
        return this.extend(new EnvFrame(ls, le));
    }

    public void set(LispSymbol sym, Object val)
        throws VariableDoesNotExistException
    {
        Environment e = this;
        while(e != null) {
            if(e.head.hasSymbol(sym)) {
                e.head.bind(sym,val);
                return;
            }
            e = e.tail;
        }
        // se si arriva a null allora
        throw new VariableDoesNotExistException
                ("cannot assign symbol " + sym.getName()
                 + " as there is no variable " + sym.getName() +
                 " in the environment");
    }

    public void define(LispSymbol sym, Object val)
    {
        if(head.hasSymbol(sym))
            ErrorFormatter.warn("redefining variable " + sym.getName());

        head.bind(sym, val);
    }

    public void reset() {
        this.head = new EnvFrame();
        this.tail = null;
    }

    void dump() {
        head.dump();
        System.out.println("---------");
        if (tail != null)
            tail.dump();
        else
            System.out.println("-THE END-");
    }
}

