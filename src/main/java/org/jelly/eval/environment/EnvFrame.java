package org.jelly.eval.environment;

import org.jelly.lang.data.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnvFrame implements Map<Symbol, Object> {
    Map<Symbol, Box> inner;

    // quando vedo se posso mutare gli oggetti sottostanti e restare parallelo metto concurrenthasMap

    public EnvFrame() {
        inner = new ConcurrentHashMap<>();
    }

    public EnvFrame(Map<Symbol, Object> nameToExpr) {
        inner = new ConcurrentHashMap<>();
        nameToExpr.forEach((key, value) -> inner.put(key, new Box(value)));
    }

    public EnvFrame(List<Symbol> names, List<Object> exprs) {
        /* expects names and exprs to have the same size
         * I would assert it, but I don't know if throwing an exception in the
         * constructor is a good idea
         */
        inner = new ConcurrentHashMap<>();
        for (int i = 0; i < names.size(); ++i) {
            inner.put(names.get(i), new Box(exprs.get(i)));
        }
    }

    // basic things I need
    @Override
    public Object get(Object s) {
        return switch(inner.get(s)) {
            case null -> null;
            case Box b -> b.get();
        };
    }

    @Override
    public Object put(Symbol s, Object newVal) {
        Object old = null;
        if(inner.containsKey(s)) {
            old = inner.get(s).get();
            inner.get(s).set(newVal);
        }
        else {
            inner.put(s, new Box(newVal));
        }
        return old;
    }

    // envframe specifics
    void dump() {
        // prints the entire state of the frame
        for (Symbol s : keySet()) {
            System.out.println(s + " : " +get(s));
        }
    }

    public Box getBox(Symbol sym) {
        return inner.get(sym);
    }

    public void putBox(Symbol sym, Box box) {
        inner.put(sym, box);
    }

    // other things I have to implement for this to be a map
    @Override
    public void clear() {
        inner.clear();
    }

    @Override
    public boolean containsKey(Object o) {
        return inner.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return inner.containsValue(new Box(o));
    }

    @Override
    public @NotNull Set<Map.Entry<Symbol, Object>> entrySet() {
        return inner.entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public @NotNull Set<Symbol> keySet() {
        return inner.keySet();
    }

    @Override
    public void putAll(@NotNull Map<? extends Symbol, ? extends Object> other) {
        // other.forEach((key, value) -> put(key, new Box(value)));
        throw new UnsupportedOperationException("EnvFrame can't putAll from a non EnvFrame map, parameter "
                + other + " not accepted");
    }

    public void putAll(EnvFrame f) {
        inner.putAll(f.inner);
    }

    @Override
    public Object remove(Object key) {
        return inner.remove(key);
    }

    @Override
    public int size() {
        return inner.size();
    }

    public @NotNull Collection<Object> values() {
        return inner.values().stream().map(Box::get).collect(Collectors.toSet());
    }
}
