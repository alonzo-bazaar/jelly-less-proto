package org.jelly.parse;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;

import org.jelly.lang.Cons;
import org.jelly.lang.Serializer;
import org.jelly.utils.StringCharIterator;
import org.jelly.lang.Constants;

import org.jelly.lang.errors.TokenParsingException;
import org.jelly.parse.errors.TokensExhaustedException;
import org.jelly.parse.errors.UnbalancedParenthesesException;

public class ExpressionIterator {
    private TokenIterator tokens;
    private Stack<Object> stack = new Stack<Object>();
    private Queue<Object> precomputed = new LinkedList<Object>();
    // with stack.peek() being the expression we're now building to add
    // somewhere in the tree of the toplevel expression that next() will yield

    public ExpressionIterator(TokenIterator tokens) {
        this.tokens = tokens;
    }

    public ExpressionIterator(Iterator<Character> chars) {
        this.tokens = new TokenIterator
            (new SignificantCharsIterator(chars));
    }

    public static ExpressionIterator fromString(String s) {
        return new ExpressionIterator(TokenIterator.fromString(s));
    }

    public void setString(String s) {
        /* TODO THIS IS VERY TEMPORARY
         * IT IS ONLY USED IN THE REPL
         * PLEASE DO NOT KEEP THIS, RESTRUCTURE THE THINGS THAT MADE THIS NECESSARY
         * SO THAT THIS MAY NO LONGER BE NECESSARY
         */
        this.tokens = new TokenIterator
            (new SignificantCharsIterator
             (new StringCharIterator(s)));
    }

    public Object next() throws TokenParsingException {
        precompute();
        if(!hasNext()) return null;
        return precomputed.remove();
    }

    Object getNext() throws TokenParsingException {
        while (tokens.hasNext()) {
            String token = tokens.next();
            if(token == null && !stack.isEmpty()) // TODO quando ristrutturi token qui ci metti un Token.isEOF()
                throw new TokensExhaustedException("tokens exhausted with incomplete form, probably due to an unclosed parenthesis");
            assert token != null;
            if (token.equals("(")) {
                stack.push(Constants.NIL);
            }
            else if (token.equals(")")) {
                if (stack.isEmpty()) {
                    throw new UnbalancedParenthesesException("end of file before parsing, closing parentheses does not match any previously open parenthesis");
                }
                Object expr = stack.pop();
                if (stack.isEmpty()) {
                    return expr;
                } else {
                    addToTop(expr);
                }
            }
            else {
                Object expr = Serializer.fromToken(token);
                if (stack.isEmpty()) {
                    return expr;
                } else {
                    addToTop(expr);
                }
            }
        }
        return null;
    }

    void precompute() throws TokenParsingException {
        Object le = getNext();
        while(le != null) {
            precomputed.add(le);
            le = getNext();
        }
    }

    public boolean hasNext() throws TokenParsingException {
        precompute();
        return !precomputed.isEmpty();
    }

    private void addToTop(Object le) throws InvalidParameterException {
        if (stack.peek() == Constants.NIL) {
            stack.pop();
            stack.push(new Cons(le, Constants.NIL));
        }
        else if (stack.peek() instanceof Cons c) {
            c.addLast(le);
        }
        else {
            throw new InvalidParameterException(
                    "cannot push" + le + " unto" + stack.peek() + " of non list type "
                            + stack.peek().getClass().getCanonicalName());
        }
    }
}

