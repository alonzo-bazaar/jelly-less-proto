package org.jelly.eval.library;

import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.Symbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ImportSet {
    private final ExportedFrame bindings;

    private ImportSet(ExportedFrame frame) {
        this.bindings = frame;
    }

    public static ImportSet library(Library lib) {
        return new ImportSet(lib.getExportedBindings());
    }

    public static ImportSet only(ImportSet orig, Collection<Symbol> only) {
        // Map<Symbol, Object> newBinds = new HashMap<>();
        // for(Symbol sym : orig.bindings.keySet()) {
        //     if(only.contains(sym)) {
        //         newBinds.put(sym, orig.bindings.get(sym));
        //     }
        // }
        // return new ImportSet(newBinds);

        return new ImportSet(orig.bindings.only(only));
    }

    public static ImportSet except(ImportSet orig, Collection<Symbol> except) {
        // Map<Symbol, Object> newBinds = new HashMap<>();
        // for(Symbol sym : orig.bindings.keySet()) {
        //     if(!except.contains(sym)) {
        //         newBinds.put(sym, orig.bindings.get(sym));
        //     }
        // }
        // return new ImportSet(newBinds);

        return new ImportSet(orig.bindings.except(except));
    }

    private static ImportSet prefix(ImportSet orig, String prefix) {
        // Map<Symbol, Object> newBinds = new HashMap<>();
        // for(Symbol sym : orig.bindings.keySet()) {
        //     newBinds.put(new Symbol(prefix.concat(sym.name())), orig.bindings.get(sym));
        // }
        // return new ImportSet(newBinds);

        return new ImportSet(orig.bindings.prefix(prefix));
    }

    public static ImportSet prefix(ImportSet orig, Symbol sym) {
        return prefix(orig, sym.name());
    }

    public static ImportSet rename(ImportSet orig, Map<Symbol, Symbol> renames) {
        // Map<Symbol, Object> newBinds = new HashMap<>();
        // for(Symbol sym : orig.bindings.keySet()) {
        //     switch(renames.get(sym)) {
        //         case null -> newBinds.put(sym, orig.bindings.get(sym));
        //         case Symbol newSym -> newBinds.put(newSym, orig.bindings.get(sym));
        //     }
        // }
        // return new ImportSet(newBinds);

        return new ImportSet(orig.bindings.rename(renames));
    }

    public static ImportSet join(ImportSet a, ImportSet b) {
        // for(Symbol key : b.bindings.keySet()) {
        //     a.bindings.put(key, b.bindings.get(key));
        // }
        // return new ImportSet(a.bindings);
        return new ImportSet(a.bindings.join(b.bindings));
    }

    public void importInto(Environment env) {
        env.push(bindings);
        // for(Symbol sym: bindings.keySet()) {
        //     env.define(sym, bindings.get(sym));
        // }
    }
}
