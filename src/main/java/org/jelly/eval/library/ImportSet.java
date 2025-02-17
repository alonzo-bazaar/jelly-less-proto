package org.jelly.eval.library;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;

import java.util.Collection;
import java.util.Map;

public class ImportSet {
    private final EnvFrame bindings;

    private ImportSet(EnvFrame frame) {
        this.bindings = frame;
    }

    public static ImportSet library(Library lib) {
        return new ImportSet(lib.getExportedBindings());
    }

    public static ImportSet only(ImportSet orig, Collection<Symbol> only) {
        EnvFrame newBinds = new EnvFrame();
        for(Symbol sym : orig.bindings.keySet()) {
            if(only.contains(sym)) {
                newBinds.put(sym, orig.bindings.get(sym));
            }
        }
        return new ImportSet(newBinds);
    }

    public static ImportSet except(ImportSet orig, Collection<Symbol> except) {
        EnvFrame newBinds = new EnvFrame();
        for(Symbol sym : orig.bindings.keySet()) {
            if(!except.contains(sym)) {
                newBinds.put(sym, orig.bindings.get(sym));
            }
        }
        return new ImportSet(newBinds);
    }

    private static ImportSet prefix(ImportSet orig, String prefix) {
        EnvFrame newBinds = new EnvFrame();
        for(Symbol sym : orig.bindings.keySet()) {
            newBinds.put(new Symbol(prefix.concat(sym.name())), orig.bindings.get(sym));
        }
        return new ImportSet(newBinds);
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
        return new ImportSet(newBinds);
    }

    public static ImportSet join(ImportSet a, ImportSet b) {
        // destructive, import sets used with join are not expected to be used outside of join, so it's fine
        a.bindings.putAll(b.bindings);
        return new ImportSet(a.bindings);
    }

    public void importInto(Environment env) {
        env.getHead().putAll(bindings);
    }
}
