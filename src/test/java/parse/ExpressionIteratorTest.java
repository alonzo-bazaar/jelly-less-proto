package parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import lang.LispExpression;
import lang.LispSymbol;
import lang.LispValue;
import lang.Cons;
import lang.Ops; // for type checks
import utils.StringCharIterator;


public class ExpressionIteratorTest {
    ExpressionIterator fromString(String s) {
        // and this, my friends, is what lisp does to a mf
        // TODO move this(and other) fromString methods to the non test classes
        // it's a bit shit here, and it doesn't make sense for this to be test logic
        // it's just logic
        return new ExpressionIterator (new TokenIterator
                                       (new SignificantCharsIterator
                                        (new StringCharIterator(s))));
    }
    @Test
    public void someSymbols() {
        ExpressionIterator ei = fromString("mamma mia");
        LispExpression le;
        LispSymbol ls;

        le = ei.next();
        assertTrue(Ops.isSymbol(le));
        ls = (LispSymbol)le;
        assertEquals(ls.getName(), "mamma");

        le = ei.next();
        assertTrue(Ops.isSymbol(le));
        ls = (LispSymbol)le;
        assertEquals(ls.getName(), "mia");

        assertFalse(ei.hasNext());
    }

    @Test
    public void someNumbers() {
        ExpressionIterator ei = fromString("20 30 0");
        LispExpression le;
        LispValue<Integer> li;
        int i;

        le = ei.next();
        assertTrue(Ops.isInteger(le));
        li = (LispValue<Integer>)le;
        i = li.get();
        assertEquals(i, 20);

        le = ei.next();
        assertTrue(Ops.isInteger(le));
        li = (LispValue<Integer>)le;
        i = li.get();
        assertEquals(i, 30);

        le = ei.next();
        assertTrue(Ops.isInteger(le));
        li = (LispValue<Integer>)le;
        i = li.get();
        assertEquals(i, 0);
    }

    @Test
    public void consesLength() {
        ExpressionIterator ei = fromString("(ei fu siccome immobile)");
        LispExpression le = ei.next();
        assertTrue(Ops.isCons(le));
        Cons c = (Cons) le;
        assertFalse(ei.hasNext());
        assertEquals(c.length(), 4);
    }

    @Test
    public void consesContents() {
        ExpressionIterator ei = fromString("(define x 20)");
        // in cons fa (cons define (cons x (cons 20 nil))),
        // stiamo testando contro questa struttura
        LispExpression le = ei.next();
        assertTrue(Ops.isCons(le));
        Cons c = (Cons) le;
        LispExpression le0 = c.nth(0);
        LispExpression le1 = c.nth(1);
        LispExpression le2 = c.nth(2);
        
        assertTrue(Ops.isSymbol(le0));
        assertEquals(((LispSymbol) le0).getName(), "define");

        assertTrue(Ops.isSymbol(le1));
        assertEquals(((LispSymbol) le1).getName(), "x");

        assertTrue(Ops.isInteger(le2));
        assertEquals((int)((LispValue<Integer>)le2).get(), 20);
    }

    @Test
    public void someNestedConses() {
        ExpressionIterator ei = fromString("((ok))");
        // in cons fa (cons (cons ok nil) nil),
        // stiamo testando contro questa struttura
        LispExpression outer = ei.next();
        assertFalse(ei.hasNext());
        assertTrue(Ops.isCons(outer));
        assertTrue(Ops.isNil(((Cons) outer).getCdr()));

        LispExpression midder = ((Cons) outer).getCar();
        assertTrue(Ops.isCons(midder));
        assertTrue(Ops.isNil(((Cons)midder).getCdr()));

        LispExpression inner = ((Cons) midder).getCar();
        assertTrue(Ops.isSymbol(inner));
        assertEquals(((LispSymbol) inner).getName(), "ok");
    }

    @Test
    public void someNestedConsesAndBefore() {
        ExpressionIterator ei = fromString("((ok))");
        // in cons fa (cons (cons ok nil) nil),
        // stiamo testando contro questa struttura
        LispExpression outer = ei.next();
        assertFalse(ei.hasNext());
        assertTrue(Ops.isCons(outer));
        assertTrue(Ops.isNil(((Cons) outer).getCdr()));

        LispExpression midder = ((Cons) outer).getCar();
        assertTrue(Ops.isCons(midder));
        assertTrue(Ops.isNil(((Cons)midder).getCdr()));

        LispExpression inner = ((Cons) midder).getCar();
        assertTrue(Ops.isSymbol(inner));
        assertEquals(((LispSymbol) inner).getName(), "ok");
    }

    @Test
    public void someNestedConsesAndAfter() {
        ExpressionIterator ei = fromString("((ok))");
        // in cons fa (cons (cons ok nil) nil),
        // stiamo testando contro questa struttura
        LispExpression outer = ei.next();
        assertFalse(ei.hasNext());
        assertTrue(Ops.isCons(outer));
        assertTrue(Ops.isNil(((Cons) outer).getCdr()));

        LispExpression midder = ((Cons) outer).getCar();
        assertTrue(Ops.isCons(midder));
        assertTrue(Ops.isNil(((Cons)midder).getCdr()));

        LispExpression inner = ((Cons) midder).getCar();
        assertTrue(Ops.isSymbol(inner));
        assertEquals(((LispSymbol) inner).getName(), "ok");
    }
}
