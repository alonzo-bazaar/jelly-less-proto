package org.jelly.eval.environment;

import java.util.List;

import org.jelly.eval.ErrorFormatter;
import org.jelly.eval.environment.errors.UnboundVariableException;
import org.jelly.eval.environment.errors.VariableDoesNotExistException;
import org.jelly.lang.data.Symbol;

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

    public Object lookup(Symbol name) {
        for(Environment env = this; env!=null; env = env.tail) {
            Object o = env.head.lookup(name);
            if(o != null) {
                return o;
            }
        }
        throw new UnboundVariableException("variable " + name.name() + " is not bound");
    }

    public Environment extend() {
        return new Environment(new EnvFrame(), this);
    }

    public Environment extend(EnvFrame frame) {
        return new Environment(frame, this);
    }

    public Environment extend(List<Symbol> ls, List<Object> le) {
        return this.extend(new EnvFrame(ls, le));
    }

    public void set(Symbol sym, Object val)
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
                ("cannot assign symbol " + sym.name()
                 + " as there is no variable " + sym.name() +
                 " in the environment");
    }

    public void define(Symbol sym, Object val)
    {
        if(head.hasSymbol(sym))
            ErrorFormatter.warn("redefining variable " + sym.name());

        head.bind(sym, val);
    }

    public void reset() {
        this.head = new EnvFrame();
        this.tail = null;
    }

    public void dump() {
        head.dump();
        System.out.println("-- next frame: --");
        if (tail != null)
            tail.dump();
        else
            System.out.println("-- the end --");
    }
}

