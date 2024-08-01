package org.jelly.eval.library;

import org.jelly.lang.data.ConsList;
import org.jelly.utils.ConsUtils;

import java.nio.channels.NoConnectionPendingException;
import java.util.HashMap;
import java.util.Map;

public class Registry {
    private final static Map<ConsList, Library> registry = new HashMap<>();

    public static void registerLibrary(ConsList ll, Library lib) {
        registry.put(ll, lib);
    }
    public static Library getLibrary(ConsList ll) {
        return switch(registry.get(ll)) {
            case null -> throw new NoSuchLibraryException("cannot access library " + ConsUtils.renderList(ll) + " as it doesn't exist");
            case Library l -> l;
        };
    }
    public static void reset() {
        registry.clear();
    }
}
