package eval;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lang.LispSymbol;

public class EnvironmentTest {
    private void hcf(String s) {
        throw new AssertionError(s);
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
        assertEquals(e.lookup(new LispSymbol("mamma")), "gianluca");
        assertEquals(e.lookup(new LispSymbol("mia")), "gianpiero");
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

        assertEquals(e.lookup(new LispSymbol("mamma")), "antonio");
    }
}

    
