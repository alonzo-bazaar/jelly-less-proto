package org.jelly.eval.environment;

import org.jelly.lang.data.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnvFrame implements Map<Symbol, Object> {
    private final Map<Symbol, Object> nameToExpr;

    public EnvFrame() {
        this.nameToExpr = new HashMap<>();
    }

    public EnvFrame(Map<Symbol, Object> nameToExpr) {
        this.nameToExpr = nameToExpr;
    }

    public EnvFrame(List<Symbol> names, List<Object> exprs) {
        this.nameToExpr = new HashMap<>();
        /* expects names and exprs to have the same size
         * I would assert it, but I don't know if throwing an exception in the
         * constructor is a good idea
         */
        for (int i = 0; i < names.size(); ++i) {
            this.nameToExpr.put(names.get(i), exprs.get(i));
        }
    }

    // Environment does all the checking, input data assumed to be valid
    @Override
    public Object get(Object sym) {
        return nameToExpr.get(sym);
    }

    @Override
    public Object put(Symbol sym, Object val) {
        Object old = nameToExpr.get(sym);
        nameToExpr.put(sym, val);
        return old;
    }

    @Override
    public boolean containsKey(Object sym) {
        return nameToExpr.containsKey(sym);
    }

    @Override
    public boolean containsValue(Object val) {
        return nameToExpr.containsValue(val);
    }

    @Override
    public @NotNull Set<Symbol> keySet() {
        return nameToExpr.keySet();
    }

    @Override
    public int size() {
        return nameToExpr.size();
    }

    @Override
    public boolean isEmpty() {
        return nameToExpr.isEmpty();
    }

    @Override
    public @NotNull Set<Entry<Symbol, Object>> entrySet() {
        return nameToExpr.entrySet();
    }

    @Override
    public Object remove(Object sym) {
        return nameToExpr.remove(sym);
    }

    @Override
    public @NotNull Collection<Object> values() {
        return nameToExpr.values();
    }

    @Override
    public void putAll(Map<? extends Symbol, ? extends Object> map) {
        nameToExpr.putAll(map);
    }

    @Override
    public void clear() {
        nameToExpr.clear();
    }

    void dump() {
        // prints the entire state of the frame
        for (Symbol s : nameToExpr.keySet()) {
            System.out.println(s + " : " + nameToExpr.get(s));
        }
    }
}
