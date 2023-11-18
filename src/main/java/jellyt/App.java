package jellyt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import eval.Runtime;
import parse.ExpressionIterator;
import parse.ParsingException;

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
        catch(FileNotFoundException fnfe) {
            System.out.println("error while looking for file : "+ f.getPath());
            System.out.println("no such file (or directory)");
            System.out.println(fnfe.getMessage());
        }
        catch(ParsingException pe) {
            System.out.println("error while parsing file" + f.getPath());
            System.out.println("synthax error");
            System.out.println(pe.getMessage());
        }
        catch(Throwable t) {
            System.out.println("some error occured, " +
                               t.getClass().getCanonicalName());
            System.out.println(t.getMessage());
        }
    }

    public static void repl() {
        Scanner scan = new Scanner(System.in);
        String str = "";
        ExpressionIterator ei = ExpressionIterator.fromString(str);

        while (true) {
            try {
                if (ei.hasNext()) {
                    Object expr = ei.next();
                    Object val = runtime.evalExpr(expr);
                    System.out.println(val);
                }
                else {
                    replPrompt();
                    if (!scan.hasNext()) {
                        scan.close();
                        break;
                    }
                    str = scan.nextLine();
                    ei.setString(str);
                }
            } catch (Throwable t) {
                System.out.println("Caugh error of type : " + t.getClass().getCanonicalName());
                System.out.println(t.getMessage());
                System.out.println("see stack trace? [y(es)/n(o)] ");
                String wanna = scan.next();
                if(wanna.toLowerCase().startsWith("y"))
                    t.printStackTrace();
            }
        }
    }

    private static void replPrompt() {
        System.out.print("eval >> ");
    }
}

