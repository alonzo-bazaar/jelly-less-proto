package org.jelly.eval.environment;

import java.nio.file.Path;

import org.jelly.eval.ErrorFormatter;
import org.jelly.eval.environment.errors.UnboundVariableException;
import org.jelly.eval.environment.errors.VariableDoesNotExistException;
import org.jelly.eval.library.LibraryRegistry;
import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.Symbol;

public class Environment {
    // definizione abbastanza poco implicita come lista concatenata
    // terminata da null perchè sì
    private EnvFrame head;
    private Environment tail = null;
    private JellyRuntime runtime = null;

    public Environment getTail() {
        return tail;
    }

    public EnvFrame getHead() {
        return head;
    }

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

    public Environment(Environment env) {
        this.head = env.head;
        this.tail = env.tail;
        this.setRuntime(env.getRuntime());
    }

    private Environment getRoot() {
        if(this.tail == null)
            return this;
        return this.tail.getRoot();
    }

    public Environment setRuntime(JellyRuntime jr) {
        this.getRoot().runtime = jr;
        return this;
    }

    public JellyRuntime getRuntime() {
        return this.getRoot().runtime;
    }

    public LibraryRegistry getLibraryRegistry () {
        return this.getRuntime().getLibraryRegistry();
    }

    public Path getCwd() {
        return this.getRuntime().resolvePath("");
    }

    public Object lookup(Symbol name) {
        for(Environment env = this; env!=null; env = env.tail) {
            if(env.head.containsKey(name))
                return env.head.get(name);
        }
        throw new UnboundVariableException("variable " + name.name() + " is not bound");
    }

    public Environment extend() {
        return new Environment(new EnvFrame(), this);
    }

    public Environment extend(EnvFrame frame) {
        return new Environment(frame, this);
    }

    public Environment push(EnvFrame frame) {
        Environment oldSelf = new Environment(this.head, this.tail);
        this.head = frame;
        this.tail = oldSelf;
        return this;
    }

    public EnvFrame pop() {
        if(this.tail == null) {
            throw new NullPointerException("environment has null tail, cannot pop any further");
        }

        // this = this.tail modificherebbe un po' di reference, temo
        Environment newTail = this.tail.tail;
        EnvFrame newHead = this.tail.head;
        EnvFrame popped = this.head;

        this.tail = newTail;
        this.head = newHead;
        return popped;
    }

    public void set(Symbol sym, Object val)
        throws VariableDoesNotExistException
    {
        Environment e = this;
        while(e != null) {
            if(e.head.containsKey(sym)) {
                e.head.put(sym,val);
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
        if(head.containsKey(sym))
            ErrorFormatter.warn("redefining variable " + sym.name());

        head.put(sym, val);
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

    public Box getBox(Symbol name) {
        for(Environment env = this; env!=null; env = env.tail) {
            if(env.head.containsKey(name))
                return env.head.getBox(name);
        }
        throw new UnboundVariableException("variable " + name.name() + " is not bound");
    }
}

