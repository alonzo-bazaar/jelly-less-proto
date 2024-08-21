package org.jelly.app;

// TODO, questa classe contiene un puttanaio di logica duplicata e scala una merda, sei pregato di risistemarla :)

import java.nio.file.Files;
import java.nio.file.Paths;

class RunningParameters {
    enum RunningMode {BATCH_MODE, REPL_MODE}
    boolean withFile = false;
    String filename = "";
    RunningMode runningMode = RunningMode.BATCH_MODE;
    String[] runtimeArgv = new String[]{}; // argv to be passed to the jelly runtime

    public static RunningParameters fromArgs(String[] args) {
        if(args.length == 0)
            return new RunningParameters().setRunningMode(RunningMode.REPL_MODE);

        else if(args.length >=2 && specifiesRunningMode(args[0]) && Files.exists(Paths.get(args[1])))
            return new RunningParameters()
                    .setRunningMode(parseRunningMode(args[0]))
                    .setFilename(args[1])
                    .setRuntimeArgv(drop(args, 2));

        else if(specifiesRunningMode(args[0]))
            return new RunningParameters()
                    .setRunningMode(parseRunningMode(args[0]))
                    .setRuntimeArgv(drop(args, 1));

        else if(Files.exists(Paths.get(args[0])))
            return new RunningParameters()
                    .setFilename(args[0])
                    .setRunningMode(RunningMode.BATCH_MODE)
                    .setRuntimeArgv(drop(args, 1));

        else return new RunningParameters()
                    .setRunningMode(RunningMode.REPL_MODE)
                    .setRuntimeArgv(args);
    }

    private RunningParameters() {

    }

    public boolean hasFile() {
        return this.withFile;
    }

    public String getFilename() {
        return this.filename;
    }

    public RunningMode getRunningMode() {
        return this.runningMode;
    }

    public String[] getRuntimeArgv() {
        return runtimeArgv;
    }


    private RunningParameters setFilename(String filename) {
        this.withFile = true;
        this.filename = filename;
        return this;
    }
    private RunningParameters setRunningMode(RunningMode rn) {
        this.runningMode = rn;
        return this;
    }
    private RunningParameters setRuntimeArgv(String[] runtimeArgv) {
        this.runtimeArgv = runtimeArgv;
        return this;
    }

    private static boolean specifiesRunningMode(String s) {
        return s.equals("repl") || s.equals("batch");
    }

    private static RunningMode parseRunningMode(String arg) {
        if(arg.equals("repl"))
            return RunningMode.REPL_MODE;

        // default
        return RunningMode.BATCH_MODE;
    }

    private static String[] drop(String[] orig, int n) {
        String[] res = new String[orig.length - n];
        for(int i = 0, j = n; j<orig.length; ++i, ++j) {
            res[i] = orig[j];
        }
        return res;
    }
}
