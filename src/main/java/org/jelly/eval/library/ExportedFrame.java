package org.jelly.eval.library;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.lang.data.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportedFrame extends EnvFrame {
    private final Library lib;
    // redirects map to allow internal library to be modified and update the values to the exterior
    // direct map to make it also work as a normal envframe
    // all map interface destructive operations are performed on the direct map
    private final Map<Symbol, Symbol> redirects = new HashMap<>();
    private final Map<Symbol, Object> direct = new HashMap<>();

    public ExportedFrame(Library lib) {
        this.lib = lib;
    }

    public void directExport(Symbol exposed, Symbol internal) {
        redirects.put(exposed, internal);
    }

    @Override
    public Object get(Object sym) {
        if(sym instanceof Symbol && redirects.containsKey(sym)) {
            Symbol internal = redirects.get(sym);
            return lib.getInternalEnv().get(internal);
        }
        return direct.get(sym);
    }

    @Override
    public Object put(Symbol sym, Object val) {
        return direct.put(sym, val);
    }

    @Override
    public boolean containsKey(Object sym) {
        return  direct.containsKey(sym) || redirects.containsKey(sym);
    }

    @Override
    public boolean containsValue(Object val) {
        return direct.containsValue(val)
                || redirects.values().stream().anyMatch(a -> lib.getInternalEnv().get(a).equals(val));
    }

    @Override
    public @NotNull Set<Symbol> keySet() {
        return Stream.concat(redirects.keySet().stream(), direct.keySet().stream()).collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return redirects.size() + direct.size();
    }

    @Override
    public boolean isEmpty() {
        return redirects.isEmpty() && direct.isEmpty();
    }

    @Override
    public @NotNull Set<Entry<Symbol, Object>> entrySet() {
        Set<Entry<Symbol, Object>> res = new HashSet<>();
        redirects.forEach((internal, external) ->
                res.add(new AbstractMap.SimpleEntry<>(external, lib.getInternalEnv().get(internal))));
        direct.forEach((key, value) ->
                res.add(new AbstractMap.SimpleEntry<>(key, value)));
        return res;
    }

    @Override
    public Object remove(Object sym) {
        return direct.remove(sym);
    }

    @Override
    public @NotNull Collection<Object> values() {
        return Stream.concat(direct.values().stream(),
                             redirects.values().stream().map(a -> lib.getInternalEnv().get(a)))
                    .collect(Collectors.toSet());
    }

    @Override
    public void putAll(Map<? extends Symbol, ? extends Object> map) {
        direct.putAll(map);
    }

    @Override
    public void clear() {
        direct.clear();
    }
}
