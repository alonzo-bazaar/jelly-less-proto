package parse;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Stack;

import lang.LispExpression;
import lang.Cons;
import lang.Serializer;
import lang.Constants; // for NIL
import lang.Ops; // for type checking

public class ExpressionIterator implements Iterator<LispExpression> {
    private TokenIterator tokens;
    private Stack<LispExpression> stack = new Stack<LispExpression>();
    // with stack.peek() being the expression we're now building to add
    // somewhere in the tree of the toplevel expression that next() will yield

    public ExpressionIterator(TokenIterator tokens) {
        this.tokens = tokens;
    }

    @Override
    public LispExpression next() {
        try {
            LispExpression le = _next();
            return le;
        } catch (UnbalancedParensException upe){
            System.out.println("paren error : " + upe.getMessage());
            upe.printStackTrace();
        } catch (TokensExhaustedException tee){
            System.out.println("tokens error : " + tee.getMessage());
            tee.printStackTrace();
        }
        return null;
        // TODO qui non so che cazzo dovrei fare
        // temo la premessa di avere un iterator qui fosse un po' cagata dall'inizio
        // intanto faccio questo, poi vedrò di avere un'API più pertinente per queste parti del codice
    }

    LispExpression _next()
        throws UnbalancedParensException, TokensExhaustedException {
        while (tokens.hasNext()) {
            String token = tokens.next();
            if (token.equals("(")) {
                stack.push(Constants.NIL);
            }
            else if (token.equals(")")) {
                if (stack.isEmpty()) {
                    throw new UnbalancedParensException("end of file before parsing, closing parentheses does not match any previously open parentheses");
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
        throw new TokensExhaustedException("tokens exhausted before being able to building form, probably due to an unclosed parentheses");
    }

    @Override
    public boolean hasNext() {
        return tokens.hasNext();
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
