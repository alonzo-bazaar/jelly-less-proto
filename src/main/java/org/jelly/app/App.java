package org.jelly.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Iterator;

import org.jelly.eval.runtime.Runtime;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.expression.ExpressionIterator;
import org.jelly.parse.token.TokenIterator;
import org.jelly.parse.token.Token;

/**
 * the jellyt package contains some sort of package level façade
 * in non gof terms, it's the front end to the whole application,
 * it parses the command line arguments and runs what those arguments say must be run
 */
public class App 
{

    private static Runtime runtime = new Runtime();
    public static void main( String[] args ) {
        // currently debugging like an ass 
        System.out.println(args.length);
        for (String arg : args) {
            System.out.println(arg);
        }
        RunningParameters runningParameters = RunningParameters.fromArgs(args);
        App.run(runningParameters);
    }

    private static void run(RunningParameters rn) {
        // extremely dummy implementation
        switch(rn.getRunningMode()) {
        case REPL_MODE:
            if(rn.hasFile()) {
                System.out.println("STARTING REPL AFTER LOADING " + rn.getFilename());
                loadFile(new File(rn.getFilename()));
                repl();
            }
            else {
                System.out.println("STARTING BARE REPL");
                repl();
            }
            break;
        case BATCH_MODE:
            if(rn.hasFile()) {
                System.out.println("BATCH EVALUATION OF " + rn.getFilename());
                loadFile(new File(rn.getFilename()));
            }
            else
                System.out.println("OK DUDE WHAT THE FUCK");
            break;
        default:
            System.out.println("I HAVE NO IDEA WHAT JUST HAPPENED BUT IT'S NOT GOOD");
        }
    }

    public static void loadFile(File f) {
        try {
            runtime.evalFile(f);
        }
        catch(FileNotFoundException noFile) {
            System.out.println("error while looking for file : "+ f.getPath());
            System.out.println("no such file (or directory)");
            System.out.println(noFile.getMessage());
        }
        catch(ParsingException parse) {
            System.out.println("error while parsing file" + f.getPath());
            System.out.println("syntax error");
            System.out.println(parse.getMessage());
        }
        catch(Throwable t) {
            System.out.println("some error occurred, " +
                               t.getClass().getCanonicalName());
            System.out.println(t.getMessage());
        }
    }

    public static void repl() {
        Scanner scan = new Scanner(System.in);
        Iterator<String> lines = new InputLinesIterator();
        Iterator<Token> tokens = new TokenIterator(lines);
        Iterator<Object> expressions = new ExpressionIterator(tokens);

        while (true) {
            try {
                replPrompt();
                if(expressions.hasNext()) {
                    Object expr = expressions.next();
                    Object val = runtime.evalExpr(expr);
                    System.out.println(val);
                }
                else {
                    System.out.println("hello, goodbye");
                    break;
                }
            } catch (Throwable t){
                if (!replHandleThrowable(t, scan)) {
                    System.out.println("ok, bye then");
                    break;
                }
            }
        }

        scan.close();
    }

    private static boolean replHandleThrowable(Throwable t, Scanner s) {
        System.out.println("caught error of type : " + t.getClass().getCanonicalName());
        System.out.println("error: " + t.getMessage());
        System.out.println("see stack trace? [y(es)/n(o)] ");

        String wannaStackTrace = s.next();
        if(wannaStackTrace.toLowerCase().startsWith("y"))
            t.printStackTrace();

        System.out.println("continue? [y(es)/n(o)] ");
        String wannaContinue = s.next();
        return wannaContinue.toLowerCase().startsWith("y");
    }

    private static void replPrompt() {
        System.out.print("eval >> ");
    }
}

