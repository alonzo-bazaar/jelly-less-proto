package org.jelly.eval.environment;

import org.jelly.eval.environment.errors.UnboundVariableException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.jelly.lang.data.LispSymbol;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testNotFoundOnEmpty() {
        Environment e = new Environment();
        assertThrows(UnboundVariableException.class, () -> e.lookup(new LispSymbol("mamma")));
        assertThrows(UnboundVariableException.class, () -> e.lookup(new LispSymbol("")));
        assertThrows(UnboundVariableException.class, () -> e.lookup(new LispSymbol("mannaggia")));
    }

    @Test
    public void testNotFoundOnWrongOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new LispSymbol("mamma"),"mia");
        } catch(Throwable t) {
            hcf("bruh");
        }

        assertThrows(UnboundVariableException.class, () -> e.lookup(new LispSymbol("mamaaaaaaa")));
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
    
