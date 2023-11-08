package eval;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

import lang.LispSymbol;

public class Environment {
    private LinkedList<EnvFrame> frames;

    public Environment() {
        this.frames = new LinkedList<EnvFrame>();
        this.frames.addLast(new EnvFrame());
    }

    // rendere pubblico questo imbratterebbe abbastanza l'interfaccia
    Environment(LinkedList<EnvFrame> frames) {
        this.frames = frames;
    }
    
    public Object lookup(LispSymbol name) {
        // mi spiace per il null ma non vedo come altro esprimere
        // "hai cercato un valore che non esisteva"
        // non ha troppo senso tirare un'eccezione qui, è un comportamento previsto
        for (EnvFrame frame : frames) {
            Object le = frame.lookup(name);
            if(le!=null)
                return le;
        }
        return null;
    }

    public Environment extend() {
        LinkedList<EnvFrame> newFrames = frames;
        newFrames.addFirst(new EnvFrame());
        return new Environment(newFrames);
    }

    public Environment extend(EnvFrame frame) {
        LinkedList<EnvFrame> newFrames = frames;
        newFrames.addFirst(frame);
        return new Environment(newFrames);
    }

    public Environment extend(List<LispSymbol> ls, List<Object> le) {
        return this.extend(new EnvFrame(ls, le));
    }

    public void set(LispSymbol sym, Object val)
        throws VariableDoesNotExistException
    {
        if(!hasSymbol(sym))
            throw new VariableDoesNotExistException
                ("cannot assign symbol " + sym.getName()
                 + " as there is no variable " + sym.getName() +
                 " in the environment");
        else
            frames.get(0).set(sym, val);
    }

    public void define(LispSymbol sym, Object val)
        throws VariableAlreadyExistsException
    {
        if(hasSymbol(sym))
            throw new VariableAlreadyExistsException
                ("cannot re define symbol " + sym.getName() +
                 " as there already is a " + sym.getName() +
                 " variable in the environment");
        else
            frames.get(0).set(sym, val);
    }

    public void reset() {
        this.frames = new LinkedList<>();
    }

    boolean hasSymbol(LispSymbol sym) {
        return frames.stream().anyMatch(f -> f.hasSymbol(sym));
    }
}

class EnvFrame {
    private HashMap<String, Object> nameToExpr;

    EnvFrame() {
        this.nameToExpr = new HashMap<>();
    }

    EnvFrame(HashMap<String,Object> nameToExpr) {
        this.nameToExpr = nameToExpr;
    }

    EnvFrame(List<LispSymbol> names, List<Object> exprs) {
        this.nameToExpr = new HashMap<>();
        /* expects names and exprs to have the same size
         * I would assert it, but I don't know if throwing an exception in the
         * constructor is a good idea
         */
        for(int i = 0; i<names.size(); ++i) {
            this.nameToExpr.put(names.get(i).getName(), exprs.get(i));
        }
    }

    // Environment does all the checking, input data assumed to be valid
    Object lookup(LispSymbol sym) {
        return nameToExpr.get(sym.getName());
    }

    boolean hasSymbol(LispSymbol sym) {
        return nameToExpr.containsKey(sym.getName());
    }

    void set(LispSymbol sym, Object val) {
        nameToExpr.put(sym.getName(), val);
    }
}

