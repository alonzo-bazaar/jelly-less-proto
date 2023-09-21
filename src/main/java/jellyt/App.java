package jellyt;

import jellyt.RunningParameters;

/**
 * the jellyt package contains some sort of package level façade
 * in non gof terms, it's the front end to the whole application,
 * it parses the command line arguments and runs what those arguments say must be run
 */
public class App 
{
    public static void main( String[] args ) {
        RunningParameters runningParameters = RunningParameters.fromArgs(args);
        App.run(runningParameters);
    }

    private static void run(RunningParameters rn) {
        // extremely dummy implementation
        switch(rn.getRunningMode()) {
        case REPL_MODE:
            if(rn.hasFile())
                System.out.println("REPL AFTER LOADING " + rn.getFilename());
            else
                System.out.println("BARE REPL");
            break;
        case BATCH_MODE:
            if(rn.hasFile())
                System.out.println("BATCH EVALUATION OF " + rn.getFilename());
            else
                System.out.println("OK DUDE WHAT THE FUCK");
            break;
        default:
            System.out.println("fuck");
        }
    }
}

