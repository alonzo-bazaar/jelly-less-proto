package org.jelly.parse;

import org.junit.jupiter.api.Test;

import org.jelly.lang.LispSymbol;
import org.jelly.lang.Cons;
import org.jelly.lang.Constants;

import static org.junit.jupiter.api.Assertions.*;
import org.jelly.lang.errors.ParsingException;
import org.jelly.parse.errors.UnbalancedParenthesesException;


public class ExpressionIteratorTest {
    @Test
    public void someSymbols() throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString("mamma mia");
        Object o;
        LispSymbol ls;

        o = ei.next();
        assertEquals(((LispSymbol)o).getName(), "mamma");

        o = ei.next();
        assertEquals(((LispSymbol)o).getName(), "mia");

        assertFalse(ei.hasNext());
    }

    @Test
    public void someNumbers() throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString("20 30 0");
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
    public void consesLength() throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString("(ei fu siccome immobile)");
        Object o = ei.next();
        assertEquals(((Cons)o).length(), 4);
    }

    @Test
    public void consesContents() throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString("(define x 20)");
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
    public void someNestedConses() throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString("((ok))");
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

    @Test
    public void testUnclosedParentheses () {
        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = ExpressionIterator.fromString(")");
                    Object o = ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = ExpressionIterator.fromString("))");
                    Object o = ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = ExpressionIterator.fromString("())");
                    Object o = ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = ExpressionIterator.fromString("()))");
                    Object o = ei.next();
                });
    }

    //@Test
    //public void someNestedConsesAndBefore() {
    //}

    //@Test
    //public void someNestedConsesAndAfter() {
    //}
}
