package org.jelly.app;

import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.jelly.parse.token.Token;
import org.jelly.parse.token.PunctuationToken.PunctuationType;
import org.jelly.parse.token.PunctuationToken;
import org.jelly.parse.token.NewTokenIterator; // ignore the name

public class ShittyReplTest {
    public static void main(String[] args) {
        InputLinesIterator lines = new InputLinesIterator();
        NewTokenIterator tokens = new NewTokenIterator(lines);
        SomeFuckingExpressionIterator exprs = new SomeFuckingExpressionIterator(tokens);

        while(exprs.hasNext()) {
            Debug.println("expression iterator has next, getting next");
            Expression e = exprs.next();
            Debug.println("in app main loop: expression is");
            e.get().stream().forEach(a -> System.out.print(a + ": "));
            Debug.println();
            Debug.println("on the loop again, oh I can't wait to get on the loop again");
        }

        System.out.println("end of execution");
    }
}

final class InputLinesIterator implements Iterator<String> {
    private Scanner scanner;
    private boolean isOpen = false;
    
    public InputLinesIterator() {
        this.scanner = new Scanner(System.in);
        this.isOpen = true;
    }

    @Override
    public String next() {
        try {
            System.out.print("> ");
            String line = scanner.nextLine();
            Debug.println("recieved line :\"" + line + "\"");
            return line;
        }
        catch(NoSuchElementException eof) {
            scanner.close();
            System.out.println("\nrecieved end of line, shutting down repl");
            System.out.println("see you soon! :)");
            isOpen = false;
            return "";
        }
    }

    @Override
    public boolean hasNext() {
        return isOpen;
    }
}

final class SomeFuckingExpressionIterator implements Iterator<Expression> {
    Iterator<Token> tokens;

    public SomeFuckingExpressionIterator(Iterator<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public Expression next() {
        List<Token> expressionTokens = new ArrayList<>();
        int open = 0;

        Debug.println("getting that next expression"); 
        expressionTokens.stream().forEach(a -> Debug.print(a + ", "));
        Debug.println();

        while(!(open == 0 && !expressionTokens.isEmpty())) {
            Debug.println("start of loop");
            Debug.println("getting next token");
            Token t = tokens.next();
            Debug.println("token: #t<" + t +
                               ">, open: #o<" + open +
                               ">, size: #s<" + expressionTokens.size() +
                               ">, ergo empty: #e<" + expressionTokens.isEmpty() +
                               ">");
            switch(t) {
            case PunctuationToken pt -> {
                switch(pt.getPunctuationType()) {
                case PunctuationType.PAREN_OPEN:
                    open++;
                    break;
                case PunctuationType.PAREN_CLOSE:
                    open--;
                    break;
                default:
                    break;
                }
                expressionTokens.addLast(t);
            }
            default -> expressionTokens.addLast(t);
            }
            Debug.println("handled token <" + t + ">, openness is now " + open);
            Debug.println("expression is currently");
            expressionTokens.stream().forEach(a -> Debug.print(a + ", "));
            Debug.println();
        }
        Debug.println("got that next expression"); 
        Debug.println("==== THAT NEXT EXPRESSION ===="); 
        expressionTokens.stream().forEach(a -> Debug.print(a + ", "));
        Debug.println("==== THAT NEXT EXPRESSION ===="); 
        Debug.println();
        Debug.println();
        return new Expression(expressionTokens);
    }

    @Override
    public boolean hasNext() {
        return tokens.hasNext();
    }
}

final class Expression {
    List<Token> list; 
    public Expression(List<Token> list) {
        this.list = list;
    }

    public List<Token> get() {
        return list;
    }
}

class Debug {
    static boolean debug = true;
    public static void print(String s) {
        if(debug)
            System.out.print(s);
    }

    public static void println() {
        if(debug)
            System.out.println();
    }

    public static void println(String s) {
        if(debug)
            System.out.println(s);
    }
}
