package org.jelly.eval.evaluable;

import org.jelly.eval.runtime.JellyRuntime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

public class BaseEvaluableTest {
    private JellyRuntime jr;

    protected Object eval(String s) {
        return jr.evalString(s);
    }

    protected Object lookup(String s) {
        return jr.get(s);
    }

    protected void define(String name, Object val) {
        jr.define(name, val);
    }

    @BeforeEach
    public void refreshEnv() {
        // jr.getLibraryRegistry().reset();
        jr = new JellyRuntime();
        jr.evalString("(import (jelly functional))");
        jr.evalString("(import (jelly list base))");
    }

    @AfterEach
    public void resetEnvironment() {
        jr = null;
    }
}
