package org.jelly.eval.library;

import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.ConsUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LibraryRegistry {
    private final Map<ConsList, LazyLibrary> registry = new HashMap<>();
    private final JellyRuntime runtime;

    public LibraryRegistry(JellyRuntime runtime) {
        this.runtime = runtime;
    }

    public void registerLibrary(ConsList ll, LazyLibrary lib) {
        registry.put(ll, lib);

    }
    public Library getLibrary(ConsList ll) {
        return switch(registry.get(ll)) {
            case null -> throw new NoSuchLibraryException("cannot access library " + ConsUtils.renderList(ll) + " as it doesn't exist");
            case LazyLibrary l -> l.get();
        };
    }
    public Library getLibrary(String... libPath) {
        return getLibrary(ConsUtils.of(Arrays.stream(libPath).map(Symbol::new).toArray()));
    }

    public boolean hasLibrary(ConsList ll) {
        return registry.containsKey(ll);
    }
    public boolean hasLibrary(String... path) {
        return hasLibrary(ConsUtils.of(Arrays.stream(path).map(Symbol::new).toArray()));
    }

    public void reset() {
        registry.clear();
    }
}
