package parse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import lang.LispSymbol;
import lang.Cons;
import lang.Constants;
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
        Object o;
        LispSymbol ls;

        o = ei.next();
        assertEquals(((LispSymbol)o).getName(), "mamma");

        o = ei.next();
        assertEquals(((LispSymbol)o).getName(), "mia");

        assertFalse(ei.hasNext());
    }

    @Test
    public void someNumbers() {
        ExpressionIterator ei = fromString("20 30 0");
        Object o;

        o = ei.next();
        assertEquals((int)((Integer)o),20);

        o = ei.next();
        assertEquals((int)((Integer)o),30);

        o = ei.next();
        assertEquals((int)((Integer)o),0);

        assertFalse(ei.hasNext());
    }

    @Test
    public void consesLength() {
        ExpressionIterator ei = fromString("(ei fu siccome immobile)");
        Object o = ei.next();
        assertEquals(((Cons)o).length(), 4);
    }

    @Test
    public void consesContents() {
        ExpressionIterator ei = fromString("(define x 20)");
        // in cons fa (cons define (cons x (cons 20 nil))),
        // stiamo testando contro questa struttura
        Object o = ei.next();
        assertFalse(ei.hasNext());

        Cons c = (Cons)o;
        Object le0 = c.nth(0);
        Object le1 = c.nth(1);
        Object le2 = c.nth(2);
        
        assertEquals(((LispSymbol)le0).getName(), "define");
        assertEquals(((LispSymbol)le1).getName(), "x");
        assertEquals((int)((Integer)le2), 20);
    }

    @Test
    public void someNestedConses() {
        ExpressionIterator ei = fromString("((ok))");
        // in cons fa (cons (cons ok nil) nil),
        // stiamo testando contro questa struttura
        Object outer = ei.next();
        assertFalse(ei.hasNext());
        assertSame(((Cons) outer).getCdr(), Constants.NIL);

        Object midder = ((Cons) outer).getCar();
        assertSame((((Cons)midder).getCdr()), Constants.NIL);

        Object inner = ((Cons) midder).getCar();
        assertEquals(((LispSymbol) inner).getName(), "ok");
    }

    //@Test
    //public void someNestedConsesAndBefore() {
    //}

    //@Test
    //public void someNestedConsesAndAfter() {
    //}
}
