package org.jelly.eval.library;
import org.jelly.eval.environment.EnvFrame;
import org.jelly.lang.data.Symbol;

import java.util.HashMap;
import java.util.Map;

public class Library {
    private EnvFrame internalEnv = new EnvFrame();
    private Map<Symbol, Object> exported = new HashMap<>();

    public Map<Symbol, Object> getExportedBindings() {
        return exported;
    }

    public Object get(Symbol symbol) {
        return exported.get(symbol);
    }

    public EnvFrame getFrame() {
        return internalEnv;
    }

    public void export(Symbol sym, Object obj) {
        exported.put(sym, obj);
    }
}
