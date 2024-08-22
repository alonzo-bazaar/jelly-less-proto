package org.jelly.eval.library;
import org.jelly.eval.environment.EnvFrame;
import org.jelly.lang.data.Symbol;

import java.util.Map;

public class Library {
    private final EnvFrame internalEnv = new EnvFrame();
    private final ExportedFrame exportedFrame = new ExportedFrame(this);

    public ExportedFrame getExportedBindings() {
        return exportedFrame;
    }

    public Object get(Symbol symbol) {
        return exportedFrame.get(symbol);
    }

    public void export(Symbol exposed, Symbol internal) {
        exportedFrame.directExport(exposed, internal);
    }

    public EnvFrame getInternalEnv() {
        return internalEnv;
    }
}
