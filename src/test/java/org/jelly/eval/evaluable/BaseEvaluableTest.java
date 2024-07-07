package org.jelly.eval.evaluable;

import org.jelly.eval.evaluable.compile.Compiler;
import org.jelly.eval.environment.Environment;
import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.parse.syntaxtree.SyntaxTreeIterator;
import org.jelly.utils.DebuggingUtils;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.jelly.parse.errors.ParsingException;
import org.jelly.eval.evaluable.compile.Compiler;

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
        jr = new JellyRuntime();
    }

    @AfterEach
    public void resetEnvironment() {
        jr = null;
    }
}
