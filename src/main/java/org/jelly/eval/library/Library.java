package org.jelly.eval.library;
import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.lang.data.Symbol;

public class Library {
    private final EnvFrame bindingsFrame = new EnvFrame();
    private final ExportedFrame exportedFrame = new ExportedFrame(this);

    // messo qui solo per debuggability
    private final Environment internalEnironment;

    public Library(Environment internalEnironment) {
        this.internalEnironment = internalEnironment;
    }

    public ExportedFrame getExportedBindings() {
        return exportedFrame;
    }

    public Object get(Symbol symbol) {
        return exportedFrame.get(symbol);
    }

    public void export(Symbol exposed, Symbol internal) {
        exportedFrame.directExport(exposed, internal);
    }

    public EnvFrame getBindngsFrame() {
        return bindingsFrame;
    }

    public Environment getInternalEnvironment() {
        return internalEnironment;
    }
}
