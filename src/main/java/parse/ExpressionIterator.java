package parse;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Stack;
import java.util.Queue;
import java.util.LinkedList;

import lang.LispExpression;
import lang.Cons;
import lang.Serializer;
import utils.StringCharIterator;
import lang.Constants; // for NIL
import lang.Ops; // for type checking

public class ExpressionIterator implements Iterator<LispExpression> {
    private TokenIterator tokens;
    private Stack<LispExpression> stack = new Stack<LispExpression>();
    private Queue<LispExpression> precomputed = new LinkedList<LispExpression>();
    // with stack.peek() being the expression we're now building to add
    // somewhere in the tree of the toplevel expression that next() will yield

    public ExpressionIterator(TokenIterator tokens) {
        this.tokens = tokens;
    }

    public ExpressionIterator(Iterator<Character> chars) {
        this.tokens = new TokenIterator
            (new SignificantCharsIterator(chars));
    }

    public ExpressionIterator (String s) {
        this.tokens = new TokenIterator
        (new SignificantCharsIterator
         (new StringCharIterator(s)));
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

    @Override
    public LispExpression next() {
        try {
            precompute();
            if(!_hasNext()) return null;
            return precomputed.remove();
        } catch (Throwable t) {
            System.out.println("error reading expression : " + t.getClass().getCanonicalName());
            System.out.println(t.getMessage());
            return null;
        }
    }

    LispExpression getNext() throws UnbalancedParensException , TokensExhaustedException{
        while (tokens.hasNext()) {
            String token = tokens.next();
            if(token == null && !stack.isEmpty()) // TODO quando ristrutturi token qui ci metti un Token.isEOF()
                throw new TokensExhaustedException("tokens exhausted with incomplete form, probably due to an unclosed parenthesis");
            if (token.equals("(")) {
                stack.push(Constants.NIL);
            }
            else if (token.equals(")")) {
                if (stack.isEmpty()) {
                    throw new UnbalancedParensException("end of file before parsing, closing parentheses does not match any previously open parenthesis");
                }
                LispExpression expr = stack.pop();
                if (stack.isEmpty()) {
                    return expr;
                } else {
                    addToTop(expr);
                }
            }
            else {
                LispExpression expr = Serializer.fromToken(token);
                if (stack.isEmpty()) {
                    return expr;
                } else {
                    addToTop(expr);
                }
            }
        }
        return null;
    }

    void precompute() throws UnbalancedParensException, TokensExhaustedException {
        LispExpression le;
        le = getNext();
        while(le != null) {
            precomputed.add(le);
            le = getNext();
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return _hasNext();
        }
        catch (Throwable t) {
            return false;
        }
    }

    boolean _hasNext() throws UnbalancedParensException, TokensExhaustedException {
        precompute();
        return !precomputed.isEmpty();
    }

    private void addToTop(LispExpression le) throws InvalidParameterException {
        if (Ops.isNil(stack.peek())) {
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

class TokensExhaustedException extends Exception {
    public TokensExhaustedException(String s) {
        super(s);
    }
}

class UnbalancedParensException extends Exception {
    public UnbalancedParensException(String s) {
        super(s);
    }
}
