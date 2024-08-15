package org.jelly.app.repl;

import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.library.Library;
import org.jelly.eval.library.Registry;
import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.ConsList;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.reading.Reading;
import org.jelly.utils.ConsUtils;

import java.util.Iterator;
import java.util.Scanner;

public class Repl {
    private Library currentLibrary = null;
    private ConsList currentLibraryName = null;

    private final PrettyPrinter pp = new PrettyPrinter();

    public void run(JellyRuntime jr) {
        Scanner scan = new Scanner(System.in);
        Iterator<Object> expressions = Reading.readingScanner(scan);

        while (true) {
            try {
                prompt();
                if(expressions.hasNext()) {
                    Object expr = expressions.next();
                    Object val = jr.evalExpr(expr);
                    System.out.println(pp.render(val));
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

    private void switchToLibrary(ConsList libName) {
        currentLibraryName = libName;
        currentLibrary = Registry.getLibrary(libName);
    }
}
