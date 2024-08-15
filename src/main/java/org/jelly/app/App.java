package org.jelly.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Iterator;

import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.parse.errors.ParsingException;
import org.jelly.parse.reading.Reading;

/**
 * the jellyt package contains some sort of package level façade
 * in non gof terms, it's the front end to the whole application,
 * it parses the command line arguments and runs what those arguments say must be run
 */
public class App 
{
    private static final JellyRuntime runtime = new JellyRuntime();
    public static void main( String[] args ) {
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
                System.out.println("BRO WHAT?");
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
        runtime.runRepl();
    }
}

