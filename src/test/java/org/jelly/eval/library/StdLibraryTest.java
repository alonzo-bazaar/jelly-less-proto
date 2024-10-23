package org.jelly.eval.library;

import org.jelly.eval.runtime.JellyRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StdLibraryTest {
    // NOTA BENE
    // questi test dipendono sia dalla struttura di jelly che dalla struttura della standard library di jelly
    // modifiche alla standard library di jelly avranno effetto su questi test
    // copiatao da BaseEvaluableTest con qualche modifica necessarie
    private JellyRuntime jr;
    private Object eval(String s) { return jr.evalString(s); }
    private Object lookup(String s) {
        return jr.get(s);
    }
    private void define(String name, Object val) {
        jr.define(name, val);
    }
    @BeforeEach
    public void refreshEnv() { jr = new JellyRuntime(); }
    @AfterEach
    public void resetEnvironment() {
        jr = null;
    }

    @Test
    public void testImportsListBase() {
        eval("(import (jelly list base))");
    }

    @Test
    public void testImportListLibrary() {
        eval("(import (jelly list library))");
    }
}
