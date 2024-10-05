package org.jelly.eval.library;
import org.jelly.eval.environment.EnvFrame;
import org.jelly.eval.environment.Environment;
import org.jelly.eval.environment.errors.UnboundVariableException;
import org.jelly.eval.runtime.error.JellyError;
import org.jelly.eval.runtime.repl.Printer;
import org.jelly.lang.data.Symbol;

import javax.swing.plaf.synth.SynthButtonUI;

public class Library {
    private final EnvFrame bindingsFrame = new EnvFrame();
    private final EnvFrame exportedFrame = new EnvFrame();

    // messo qui solo per debuggability
    // poi meglio se va a sostituire il frame, redesign dopo
    private final Environment internalEnironment;

    public Library(Environment internalEnironment) {
        this.internalEnironment = internalEnironment;
    }

    public EnvFrame getExportedBindings() {
        return exportedFrame;
    }

    public Object get(Symbol symbol) {
        return internalEnironment.lookup(symbol);
    }

    public void export(Symbol exposed, Symbol internal) {
        exportedFrame.putBox(exposed, internalEnironment.getBox(internal));
    }

    public EnvFrame getBindngsFrame() {
        return bindingsFrame;
    }

    public Environment getInternalEnvironment() {
        return internalEnironment;
    }
}
