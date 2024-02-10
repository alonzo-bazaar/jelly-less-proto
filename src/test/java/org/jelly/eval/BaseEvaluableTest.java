package org.jelly.eval;

import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.compile.Compiler;
import org.jelly.eval.runtime.Environment;
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

public class BaseEvaluableTest {
    protected Environment env = new Environment();

    protected Evaluable fromString(String s) throws ParsingException {
        SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings(s);
        Object le = ei.next();
        return Compiler.compileExpression(le);
    }

    @BeforeEach
    public void refreshEnv() {
        env = new JellyRuntime().buildInitialEnvironment();
    }

    @AfterEach
    public void resetEnvironment() {
        env.reset();
    }
}
