package org.jelly.eval.runtime;

import org.jelly.eval.ErrorFormatter;
import org.jelly.eval.environment.Environment;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.library.Library;
import org.jelly.eval.runtime.repl.Printer;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.reading.Reading;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Scanner;

public class Repl {
    private Library currentLibrary = null;
    private ConsList currentLibraryName = null;

    private final Scanner scan;
    private final Iterator<Object> expressions;

    private final JellyRuntime jr;
    private Environment oldEnv = null;

    public Repl(Scanner scan, JellyRuntime jr) {
        this.scan = scan;
        this.expressions = Reading.readingScanner(scan);
        this.jr = jr;
    }
    public Repl(JellyRuntime jr) {
        this.jr = jr;
        this.scan = new Scanner(System.in);
        this.expressions = Reading.readingScanner(scan);
    }

    private Object getNext() {
        if(expressions.hasNext())
            return expressions.next();

        else throw new NullPointerException("expression iterator exhausted, cannot get next expression");
    }

    public void run() {
        while (true) {
            try {
                prompt();
                if(expressions.hasNext()) {
                    Object expr = expressions.next();
                    if(expr instanceof Symbol s && s.name().equals(":")) {
                        Symbol macroSym = (Symbol)getNext();
                        performMacro(macroSym);
                    }
                    else {
                        Object val = jr.evalExpr(expr);
                        System.out.println(Printer.render(val));
                    }
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
            System.out.print("eval" + Printer.renderList(currentLibraryName) + " >> ");
        }
    }

    private void inLibrary(ConsList libName) {
        if(oldEnv != null) {
            ErrorFormatter.warn("already in library, cannot run :library macro");
            return;
        }
        currentLibraryName = libName;
        currentLibrary = jr.getLibraryRegistry().getLibrary(libName);
        oldEnv = jr.getEnv();
        jr.setEnv(currentLibrary.getInternalEnvironment());
        System.out.println("you are how inside library " + Printer.renderList(libName));
    }

    private void outLibraray() {
        if(oldEnv == null) {
            ErrorFormatter.warn("already outside of a library, cannot run :outlibrary macro");
            return;
        }
        currentLibraryName = Constants.NIL;
        currentLibrary = null;
        jr.setEnv(oldEnv);
        this.oldEnv = null;
    }

    private void performMacro(Symbol macro) {
        switch(macro.name()) {
            case "time":
                Object expr = getNext();
                long time = System.nanoTime();
                Object val = jr.evalExpr(expr);
                time = System.nanoTime() - time;
                System.out.println(Printer.render(val));
                System.out.println(time + " nanos/ " + time/1_000 + " micros/ " + time/1_000_000 + " millis");
                break;
            case "library":
                if(currentLibrary != null) {
                    ErrorFormatter.warn("already in library " + Printer.renderList(currentLibraryName) + " cannot go further in (yet)");
                    return;
                }
                Object lib = getNext();
                if(lib instanceof ConsList liblist) {
                    inLibrary(liblist);
                }
                else
                    throw new InvalidParameterException("object " + lib + " of type " + lib.getClass().getCanonicalName()
                            + " cannot access as index to a library, only lists can be used to index a libraray");
                break;
            case "outlibrary":
                if(currentLibrary == null) {
                    ErrorFormatter.warn("cannot go out of library, currently not in a library");
                    return;
                }
                outLibraray();
                break;
            default:
                throw new InvalidParameterException("repl macro " + macro.name() + " does not exist, please don't");
        }
    }
}
