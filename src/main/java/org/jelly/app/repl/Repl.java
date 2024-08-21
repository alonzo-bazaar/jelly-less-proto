package org.jelly.app.repl;

import org.jelly.eval.ErrorFormatter;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.library.Library;
import org.jelly.eval.library.Registry;
import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.reading.Reading;
import org.jelly.utils.ConsUtils;

import java.beans.Expression;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Scanner;

public class Repl {
    private Library currentLibrary = null;
    private ConsList currentLibraryName = null;

    private final Scanner scan;
    private final Iterator<Object> expressions;

    public Repl() {
        this.scan = new Scanner(System.in);
        this.expressions = Reading.readingScanner(scan);
    }

    private Object getNext() {
        if(expressions.hasNext())
            return expressions.next();

        else throw new NullPointerException("expression iterator exhausted, cannot get next expression");
    }

    public void run(JellyRuntime jr) {
        while (true) {
            try {
                prompt();
                if(expressions.hasNext()) {
                    Object expr = expressions.next();
                    if(expr instanceof Symbol s && s.name().equals(":")) {
                        Symbol macro = (Symbol)getNext();
                        performMacro(macro, jr);
                    }
                    Object val = jr.evalExpr(expr);
                    System.out.println(Printer.render(val));
                }
                else {
                    System.out.println("hello, goodbye");
                    break;
                }
            } catch (Throwable t){
                if (!handleThrowable(t, scan)) {
                    System.out.println("ok, bye then");
                    break;
                }
            }
        }

        scan.close();
    }

    private boolean handleThrowable(Throwable t, Scanner s) {
        if(t instanceof MalformedFormException || t instanceof ParsingException) {
            System.out.println("PARSER ERROR : ");
            while(t.getCause() != null) {
                System.out.println(t);
                t = t.getCause();
            }
        }

        System.out.println("caught error of type : " + t.getClass().getCanonicalName());
        System.out.println("error: " + t.getMessage());
        System.out.println("see stack trace? [y(es)/n(o)] ");

        String wannaStackTrace = s.next();
        if(wannaStackTrace.toLowerCase().startsWith("y")) {
            t.printStackTrace();
        }

        System.out.println("continue? [y(es)/n(o)] ");
        String wannaContinue = s.next();
        return wannaContinue.toLowerCase().startsWith("y");
    }

    private void prompt() {
        if(currentLibrary == null) {
            System.out.print("eval >> ");
        }
        else {
            System.out.print("eval(" + ConsUtils.renderList(currentLibraryName) + ") >> ");
        }
    }

    private void switchToLibrary(ConsList libName, JellyRuntime jr) {
        currentLibraryName = libName;
        currentLibrary = Registry.getLibrary(libName);
        jr.inLibrary(currentLibrary);
    }

    private void outLibraray(JellyRuntime jr) {
        currentLibraryName = Constants.NIL;
        currentLibrary = null;
        jr.outLibarary();
    }

    private void performMacro(Symbol macro, JellyRuntime jr) {
        switch(macro.name()) {
            case "time":
                Object expr = getNext();
                long time = System.nanoTime();
                Object val = jr.evalExpr(expr);
                time = System.nanoTime() - time;
                System.out.println(Printer.render(val));
                System.out.println(time + "nanos/ " + time/1_000 + " micros/ " + time/1_000_000 + " millis");
            case "library":
                if(currentLibrary != null) {
                    ErrorFormatter.warn("already in library " + Printer.renderList(currentLibraryName) + " cannot go further in (yet)");
                    return;
                }
                Object lib = getNext();
                if(lib instanceof ConsList liblist) {
                    switchToLibrary(liblist, jr);
                }
                else
                    throw new InvalidParameterException("object " + lib + " of type " + lib.getClass().getCanonicalName()
                            + " cannot access as index to a library, only lists can be used to index a libraray");
            case "outlibrary":
            default:
                throw new InvalidParameterException("repl macro " + macro.name() + " does not exist, please don't");
        }
    }
}
