package org.jelly.eval;

import org.jelly.eval.runtime.Environment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jelly.lang.data.LispSymbol;

public class EnvironmentTest {
    private void hcf(String s) {
        throw new AssertionError(s);
    }

    private Environment e;
    @BeforeEach
    public void initEmptyEnv() {
        e = new Environment();
    }

    @AfterEach
    public void resetEnv() {
        e.reset();
    }

    @Test
    public void testNullOnEmpty() {
        Environment e = new Environment();
        assertNull(e.lookup(new LispSymbol("mamma")));
        assertNull(e.lookup(new LispSymbol("")));
        assertNull(e.lookup(new LispSymbol("mannaggia")));
    }

    @Test
    public void testNullOnWrongOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new LispSymbol("mamma"),"mia");
        } catch(Throwable t) {
            hcf("bruh");
        }

        Object le = e.lookup(new LispSymbol("mamaaaaaaa"));
        assertNull(le);
    }

    @Test
    public void testDefineOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new LispSymbol("mamma"), "gianluca");
            e.define(new LispSymbol("mia"), "gianpiero");
        } catch(Throwable t) {
            hcf("bruh");
        }
        assertEquals("gianluca", e.lookup(new LispSymbol("mamma")));
        assertEquals("gianpiero", e.lookup(new LispSymbol("mia")));
    }

    @Test
    public void testSetOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new LispSymbol("mamma"),"gianluca");
            e.set(new LispSymbol("mamma"), "antonio");
        } catch(Throwable t) {
            hcf("bruh");
        }

        assertEquals("antonio", e.lookup(new LispSymbol("mamma")));
    }
}
    
