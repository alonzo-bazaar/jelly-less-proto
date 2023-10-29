package eval;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import lang.LispExpression;
import lang.LispValue;
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
            e.define(new LispSymbol("mamma"), new LispValue<String>("mia"));
        } catch(Throwable t) {
            hcf("bruh");
        }

        LispExpression le = e.lookup(new LispSymbol("mamaaaaaaa"));
        assertNull(le);
    }

    @Test
    public void testDefineOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new LispSymbol("mamma"), new LispValue<String>("gianluca"));
            e.define(new LispSymbol("mia"), new LispValue<String>("gianpiero"));
        } catch(Throwable t) {
            hcf("bruh");
        }

        LispExpression le1 = e.lookup(new LispSymbol("mamma"));
        LispExpression le2 = e.lookup(new LispSymbol("mia"));
        if(le1 instanceof LispValue lv1 && lv1.get() instanceof String s) {
            assertEquals(s, "gianluca");
        }
        else hcf("not even a string bruv");

        if(le2 instanceof LispValue lv2 && lv2.get() instanceof String s) {
            assertEquals(s, "gianpiero");
        }
        else hcf("not even a string bruv");
    }

    @Test
    public void testSetOneFrame() {
        Environment e = new Environment();
        try {
            e.define(new LispSymbol("mamma"), new LispValue<String>("gianluca"));
            e.set(new LispSymbol("mamma"), new LispValue<String>("antonio"));
        } catch(Throwable t) {
            hcf("bruh");
        }

        LispExpression le = e.lookup(new LispSymbol("mamma"));
        if(le instanceof LispValue lv && lv.get() instanceof String s) {
            assertEquals(s, "antonio");
        }
        else hcf("not even a string bruv");
    }
}

    
