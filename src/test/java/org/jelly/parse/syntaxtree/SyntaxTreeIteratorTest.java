package org.jelly.parse.syntaxtree;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.errors.UnbalancedParenthesesException;
import org.jelly.utils.DebuggingUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SyntaxTreeIteratorTest {
    @Test
    public void testSymbols() throws ParsingException {
        SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("mamma mia");
        Object o;
        Symbol ls;

        o = ei.next();
        assertEquals("mamma", ((Symbol)o).name());

        o = ei.next();
        assertEquals("mia", ((Symbol)o).name());

        assertFalse(ei.hasNext());
    }

    @Test
    public void testIntegers() throws ParsingException {
        SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("20 30 0");
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
    public void testConsLenght() throws ParsingException {
        SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("(ei fu siccome immobile)");
        Object o = ei.next();
        assertEquals(4, ((Cons)o).length());
    }

    @Test
    public void testConsContents() throws ParsingException {
        SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("(define x 20)");
        // in cons fa (cons define (cons x (cons 20 nil))),
        // stiamo testando contro questa struttura
        Object o = ei.next();
        assertFalse(ei.hasNext());

        Cons c = (Cons)o;
        Object le0 = c.nth(0);
        Object le1 = c.nth(1);
        Object le2 = c.nth(2);

        assertEquals("define", ((Symbol)le0).name());
        assertEquals("x", ((Symbol)le1).name());
        assertEquals(20, (int)le2);
    }

    @Test
    public void testNestedConses() throws ParsingException {
        SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("((ok))");
        // in cons fa (cons (cons ok nil) nil),
        // stiamo testando contro questa struttura
        Object outer = ei.next();
        assertFalse(ei.hasNext());
        assertSame(Constants.NIL, ((Cons) outer).getCdr());

        Object midder = ((Cons) outer).getCar();
        assertSame(Constants.NIL, (((Cons)midder).getCdr()));

        Object inner = ((Cons) midder).getCar();
        assertEquals("ok", ((Symbol) inner).name());
    }

    @Test
    public void testThrowsOnUnbalancedParentheses () {
        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings(")");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("))");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("())");
                    ei.next();
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("()))");
                    ei.next();
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("(");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("((");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("(()");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = DebuggingUtils.expressionsFromStrings("((()");
                    ei.next();
                });
    }
}
