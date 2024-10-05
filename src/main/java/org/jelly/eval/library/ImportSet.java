package org.jelly.eval.library;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;

import java.util.Collection;
import java.util.Map;

public class ImportSet {
    private final ConsList libraryName;
    private final EnvFrame bindings;

    private ImportSet(EnvFrame frame, ConsList name) {
        this.bindings = frame;
        this.libraryName = name;
    }

    public static ImportSet library(Library lib, ConsList libraryName) {
        return new ImportSet(lib.getExportedBindings(), libraryName);
    }

    public static ImportSet only(ImportSet orig, Collection<Symbol> only) {
        EnvFrame newBinds = new EnvFrame();
        for(Symbol sym : orig.bindings.keySet()) {
            if(only.contains(sym)) {
                newBinds.put(sym, orig.bindings.get(sym));
            }
        }
        return new ImportSet(newBinds, orig.libraryName);
    }

    public static ImportSet except(ImportSet orig, Collection<Symbol> except) {
        EnvFrame newBinds = new EnvFrame();
        for(Symbol sym : orig.bindings.keySet()) {
            if(!except.contains(sym)) {
                newBinds.put(sym, orig.bindings.get(sym));
            }
        }
        return new ImportSet(newBinds, orig.libraryName);
    }

    private static ImportSet prefix(ImportSet orig, String prefix) {
        EnvFrame newBinds = new EnvFrame();
        for(Symbol sym : orig.bindings.keySet()) {
            newBinds.put(new Symbol(prefix.concat(sym.name())), orig.bindings.get(sym));
        }
        return new ImportSet(newBinds, orig.libraryName);
    }

    public static ImportSet prefix(ImportSet orig, Symbol sym) {
        return prefix(orig, sym.name());
    }

    public static ImportSet rename(ImportSet orig, Map<Symbol, Symbol> renames) {
        EnvFrame newBinds = new EnvFrame();
        for(Symbol sym : orig.bindings.keySet()) {
            switch(renames.get(sym)) {
                case null -> newBinds.put(sym, orig.bindings.get(sym));
                case Symbol newSym -> newBinds.put(newSym, orig.bindings.get(sym));
            }
        }
        return new ImportSet(newBinds, orig.libraryName);
    }

    public static ImportSet join(ImportSet a, ImportSet b) {
        // destructive, import sets used with join are not expected to be used outside of join, so it's fine
        a.bindings.putAll(b.bindings);
        return new ImportSet(a.bindings, a.libraryName);
    }

    public void importInto(Environment env) {
        env.getHead().putAll(bindings);
    }

    public ConsList getLibraryName() {
        return libraryName;
    }
}
