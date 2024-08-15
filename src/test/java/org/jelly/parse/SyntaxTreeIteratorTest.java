package org.jelly.parse;

import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.errors.UnbalancedParenthesesException;
import org.jelly.parse.syntaxtree.SyntaxTreeIterator;
import org.jelly.utils.ConsUtils;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.synth.SynthButtonUI;

import static org.junit.jupiter.api.Assertions.*;

public class SyntaxTreeIteratorTest extends BaseParserTest {
    @Test
    public void testSymbols() throws ParsingException {
        SyntaxTreeIterator ei = expressionsFromLines("mamma mia");
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
        SyntaxTreeIterator ei = expressionsFromLines("20 30 0");
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
        SyntaxTreeIterator ei = expressionsFromLines("(ei fu siccome immobile)");
        Object o = ei.next();
        assertEquals(4, ((Cons)o).length());
    }

    @Test
    public void testConsContents() throws ParsingException {
        SyntaxTreeIterator ei = expressionsFromLines("(define x 20)");
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
        SyntaxTreeIterator ei = expressionsFromLines("((ok))");
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
    public void testQuote() {
        SyntaxTreeIterator ei = expressionsFromLines("(id 'a)");
        assertEquals(ConsUtils.of(new Symbol("id"), new Symbol("'"), new Symbol("a")),
                ei.next());
        assertFalse(ei.hasNext());
    }

    @Test
    public void testThrowsOnUnbalancedParentheses () {
        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines(")");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines("))");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines("())");
                    ei.next();
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines("()))");
                    ei.next();
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines("(");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines("((");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines("(()");
                    ei.next();
                });

        assertThrows(UnbalancedParenthesesException.class,
                () -> {
                    SyntaxTreeIterator ei = expressionsFromLines("((()");
                    ei.next();
                });
    }
}
