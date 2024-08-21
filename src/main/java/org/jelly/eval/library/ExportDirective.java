package org.jelly.eval.library;

import org.jelly.lang.data.Symbol;

import java.util.Map;

public class ExportDirective {
    private final Map<Symbol, Symbol> directives;
    public ExportDirective(Map<Symbol, Symbol> directives) {
        this.directives = directives;
    }

    public Symbol exportTo(Symbol orig) {
        return directives.get(orig);
    }

    public void directLibrary(Library l) {
        for(Symbol key : directives.keySet()) {
            l.export(directives.get(key), key);
        }
    }
}
