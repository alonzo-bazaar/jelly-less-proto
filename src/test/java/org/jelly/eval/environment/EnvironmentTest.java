package org.jelly.eval.environment;

import org.jelly.eval.environment.errors.UnboundVariableException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.jelly.lang.data.Symbol;

import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentTest {
    private void hcf(String s, Throwable t) {
        throw new AssertionError(s, t);
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
        assertThrows(UnboundVariableException.class, () -> e.lookup(new Symbol("mamma")));
        assertThrows(UnboundVariableException.class, () -> e.lookup(new Symbol("")));
        assertThrows(UnboundVariableException.class, () -> e.lookup(new Symbol("mannaggia")));
    }

    @Test
    public void testNotFoundOnWrongOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new Symbol("mamma"),"mia");
        } catch(Throwable t) {
            hcf("bruh", t);
        }

        assertThrows(UnboundVariableException.class, () -> e.lookup(new Symbol("mamaaaaaaa")));
    }

    @Test
    public void testDefineOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new Symbol("mamma"), "gianluca");
            e.define(new Symbol("mia"), "gianpiero");
        } catch(Throwable t) {
            hcf("bruh", t);
        }
        assertEquals("gianluca", e.lookup(new Symbol("mamma")));
        assertEquals("gianpiero", e.lookup(new Symbol("mia")));
    }

    @Test
    public void testSetOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new Symbol("mamma"),"gianluca");
            e.set(new Symbol("mamma"), "antonio");
        } catch(Throwable t) {
            hcf("bruh", t);
        }

        assertEquals("antonio", e.lookup(new Symbol("mamma")));
    }
}
    
