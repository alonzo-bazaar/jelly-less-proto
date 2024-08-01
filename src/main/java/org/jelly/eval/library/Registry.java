package org.jelly.eval.library;

import org.jelly.lang.data.ConsList;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private final static Map<ConsList, Library> registry = new HashMap<>();

    public static void registerLibrary(ConsList ll, Library lib) {
        registry.put(ll, lib);
    }
    public static Library getLibrary(ConsList ll) {
        return registry.get(ll);
    }
    public static void reset() {
        registry.clear();
    }
}
