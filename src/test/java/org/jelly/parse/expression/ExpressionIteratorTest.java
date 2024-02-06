package org.jelly.parse.expression;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.LispSymbol;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.errors.UnbalancedParenthesesException;
import org.jelly.utils.DebuggingUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionIteratorTest {
    @Test
    public void someSymbols() throws ParsingException {
        ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("mamma mia");
        Object o;
        LispSymbol ls;

        o = ei.next();
        assertEquals("mamma", ((LispSymbol)o).getName());

        o = ei.next();
        assertEquals("mia", ((LispSymbol)o).getName());

        assertFalse(ei.hasNext());
    }

    @Test
    public void someNumbers() throws ParsingException {
        ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("20 30 0");
        Object o;

        o = ei.next();
        assertEquals(20, (int)o);

        o = ei.next();
        assertEquals(30, (int)o);

        o = ei.next();
        assertEquals(0, (int)o);

        assertFalse(ei.hasNext());
    }

    @Test
    public void consesLength() throws ParsingException {
        ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("(ei fu siccome immobile)");
        Object o = ei.next();
        assertEquals(4, ((Cons)o).length());
    }

    @Test
    public void consesContents() throws ParsingException {
        ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("(define x 20)");
        // in cons fa (cons define (cons x (cons 20 nil))),
        // stiamo testando contro questa struttura
        Object o = ei.next();
        assertFalse(ei.hasNext());

        Cons c = (Cons)o;
        Object le0 = c.nth(0);
        Object le1 = c.nth(1);
        Object le2 = c.nth(2);

        assertEquals("define", ((LispSymbol)le0).getName());
        assertEquals("x", ((LispSymbol)le1).getName());
        assertEquals(20, (int)le2);
    }

    @Test
    public void someNestedConses() throws ParsingException {
        ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("((ok))");
        // in cons fa (cons (cons ok nil) nil),
        // stiamo testando contro questa struttura
        Object outer = ei.next();
        assertFalse(ei.hasNext());
        assertSame(Constants.NIL, ((Cons) outer).getCdr());

        Object midder = ((Cons) outer).getCar();
        assertSame(Constants.NIL, (((Cons)midder).getCdr()));

        Object inner = ((Cons) midder).getCar();
        assertEquals("ok", ((LispSymbol) inner).getName());
    }

    @Test
    public void testUnclosedParentheses () {
        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings(")");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("))");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("())");
                    ei.next();
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("()))");
                    ei.next();
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("(");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("((");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("(()");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    ExpressionIterator ei = DebuggingUtils.expressionsFromStrings("((()");
                    ei.next();
                });
    }
}
