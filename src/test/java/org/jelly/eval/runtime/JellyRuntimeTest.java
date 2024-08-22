package org.jelly.eval.runtime;

import org.jelly.eval.library.Registry;
import org.jelly.lang.data.Symbol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JellyRuntimeTest {
    @Test
    public void testGetDefine() {
        JellyRuntime j = new JellyRuntime();
        j.define("a", 10);
        assertEquals(10, (int)j.get("a"));
    }

    @Test
    public void testGetSet() {
        JellyRuntime j = new JellyRuntime();
        j.define("a", 10);
        j.set("a", 20);
        assertEquals(20, (int)j.get("a"));
    }

    @Test
    public void testUpdateEval() {
        JellyRuntime j = new JellyRuntime();
        j.define("a", 10);
        assertEquals(20, (int)j.evalString("(+ a 10)"));
        j.set("a", 5);
        assertEquals(15, (int)j.evalString("(+ a 10)"));
    }

    @Test
    public void testEvalMultipleStrings() {
        JellyRuntime j = new JellyRuntime();
        j.evalString("(define a (+ 2 3))");
        j.evalString("(define (square x) (* x x))");
        assertEquals(25, (int)j.evalString("(square a)"));
    }

    @Test
    public void testMultipleRuntimes() {
        JellyRuntime j1 = new JellyRuntime();
        JellyRuntime j2 = new JellyRuntime();
        j1.define("a", 10);
        j2.define("a", 20);
        assertEquals( 10, (int)j1.get("a"));
        assertEquals( 20, (int)j2.get("a"));
        j1.set("a", 15);
        assertEquals( 20, (int)j2.get("a"));
    }

    @Test
    public void testDefineInLibraryLibraryValue() {
        JellyRuntime jr = new JellyRuntime();
        jr.evalString("(define-library (ok) (export ok))");
        jr.defineInLibrary("ok", 2, Registry.getLibrary("ok"));
        assertEquals(2, Registry.getLibrary("ok").getInternalEnv().get(new Symbol("ok")));
    }

    @Test
    public void testDefineInLibraryImportedValue() {
        JellyRuntime jr = new JellyRuntime();
        jr.evalString("(define-library (ok) (export ok))");
        jr.evalString("(import (ok))");
        jr.defineInLibrary("ok", 2, Registry.getLibrary("ok"));
        assertEquals(2, jr.evalString("ok"));
    }
}
