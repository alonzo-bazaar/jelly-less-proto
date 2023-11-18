package eval;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import lang.LispSymbol;

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
        if(!hasSymbol(sym))
            throw new VariableDoesNotExistException
                ("cannot assign symbol " + sym.getName()
                 + " as there is no variable " + sym.getName() +
                 " in the environment");
        else
            head.set(sym, val);
    }

    public void define(LispSymbol sym, Object val)
        throws VariableAlreadyExistsException
    {
        /* allow shadowing, but forbid defining the same thing twice
         * in the same lexical scope
         * I don't know
         */
        if(head.hasSymbol(sym))
            throw new VariableAlreadyExistsException
                ("cannot re define symbol " + sym.getName() +
                 " as there already is a " + sym.getName() +
                 " variable in the current scope");
        else
            head.set(sym, val);
    }

    public void reset() {
        this.head = new EnvFrame();
        this.tail = null;
    }

    boolean hasSymbol(LispSymbol sym) {
        return head.hasSymbol(sym) || (tail != null && tail.hasSymbol(sym));
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

class EnvFrame {
    private Map<String, Object> nameToExpr;

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

    void dump() {
        // prints the entire state of the frame
        for (String s : nameToExpr.keySet()) {
            System.out.println(s.toString() + " : " + nameToExpr.get(s));
        }
    }
}

