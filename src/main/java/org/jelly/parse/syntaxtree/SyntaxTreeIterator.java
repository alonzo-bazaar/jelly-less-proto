package org.jelly.parse.syntaxtree;

import java.util.Iterator;
import java.util.Stack;

import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.ListBuilder;

import org.jelly.parse.errors.UnbalancedParenthesesException;
import org.jelly.parse.token.*;

import javax.swing.plaf.synth.SynthButtonUI;


public class SyntaxTreeIterator implements Iterator<Object> {
    private final Iterator<Token> tokens;
    private final Stack<ListBuilder> stack = new Stack<>();

    public SyntaxTreeIterator(Iterator<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Object next() {
        /*
         * returns next toplevel expression in the program whose tokens are this.tokens,
         * the next expression is returned as java.lang.Object,
         * as the expression can either be an atom (that is, a string, an integer, a character)
         * or a list (so cons(some_object, some_object))
         *
         * builds the current expression tree while "visiting it" in an inorder traversal
         * returns the tree once it has visited everything, aka once it "pops back to the root"
         *
         * "tree" could also be a node with no children to its name, that is, a toplevel atomic expression
         * in that case we just return the expression
         */
        if(!hasNext())
            return null;

        boolean stack_done = false;
        while(tokens.hasNext()) {
            Token t = tokens.next();
            switch(t) {
                case null -> {
                    throw new RuntimeException("how did you even send a null token?");
                }
                case EOFToken eof -> {
                    if(stack.isEmpty())
                        return Constants.NIL;
                    else
                        throw new UnbalancedParenthesesException("parser reached end of file before expression was finished, this was probably caused by an unclosed parentheses");
                }
                case PunctuationToken pt -> {
                    switch (pt.getPunctuationType()) {
                        case PunctuationToken.PunctuationType.PAREN_OPEN:
                            stack.add(new ListBuilder());
                            break;
                        case PunctuationToken.PunctuationType.PAREN_CLOSE:
                            if (stack.isEmpty())
                                throw new UnbalancedParenthesesException("end of file before parsing, closing parentheses does not match any previously open parenthesis");
                            else if (stack.size() == 1)
                                return stack.pop().get();
                            else {
                                ListBuilder lb = stack.pop();
                                stack.peek().addLast(lb.get());
                            }
                            break;
                        case PunctuationToken.PunctuationType.QUOTE:
                            if(stack.isEmpty())
                                return new Symbol("'");
                            else
                                stack.peek().addLast(new Symbol("'"));
                            break;
                        default:
                            // non ho ancora implementato un reader, quindi quote, comma, backquote etc. non fanno
                            // e non ho ancora implementato un sistema di package, quindi colon e double colon non fanno
                            // :(
                            throw new RuntimeException("cannot have punctuation token " + t.getString() + " in an a expression (yet)");
                    }
                }
                default -> {
                    if (stack.isEmpty())
                        // we are not in a nested subexpression, toplevel atom, return the atom
                        return (asObject(t));
                    else
                        stack.peek().addLast(asObject(t));
                }
            }
        }
        throw new UnbalancedParenthesesException("parser reached end of tokens before expression was finished, this was probably caused by an unclosed parentheses, also how did you miss the end of file?");
    }

    private Object asObject(Token t) {
        return switch(t) {
            case NormalToken nt -> new Symbol(nt.getString());
            case LiteralToken<?> lt -> lt.getVal();
            default -> throw new RuntimeException("token " + t.getString() + " cannot be interpreted as an object");
        };
    }

    @Override
    public boolean hasNext() {
        return tokens.hasNext();
    }
}
