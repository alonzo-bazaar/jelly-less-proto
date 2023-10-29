package jellyt;

import java.util.Scanner;

import eval.Machine;

/**
 * the jellyt package contains some sort of package level façade
 * in non gof terms, it's the front end to the whole application,
 * it parses the command line arguments and runs what those arguments say must be run
 */
public class App 
{
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
            if(rn.hasFile())
                System.out.println("STARTING REPL AFTER LOADING " + rn.getFilename());
            else {
                System.out.println("STARTING BARE REPL");
                repl();
            }
            break;
        case BATCH_MODE:
            if(rn.hasFile())
                System.out.println("BATCH EVALUATION OF " + rn.getFilename());
            else
                System.out.println("OK DUDE WHAT THE FUCK");
            break;
        default:
            System.out.println("I HAVE NO IDEA WHAT JUST HAPPENED BUT IT'S NOT GOOD");
        }
    }

    public static void repl() {
        Machine m = new Machine();
        Scanner scan = new Scanner(System.in);
        String str;

        while (true) {
            try {
                replPrompt();
                str = scan.nextLine();
                Object obj = m.evalString(str);
                System.out.println(obj);
            } catch (Throwable t) {
                System.out.println("Oooooops!!! an exception happened");
                System.out.println("of type : " + t.getClass().getCanonicalName());
                System.out.println("saying :  " + t.getMessage());
                System.out.println("want to see a stack trace? [y(es)/n(o)] ");
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

