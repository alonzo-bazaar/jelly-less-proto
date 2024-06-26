package org.jelly.app;

enum RunningMode {BATCH_MODE, REPL_MODE}

class RunningParameters {
    boolean withFile = false;
    String filename = "";
    RunningMode runningMode;

    public static RunningParameters fromArgs(String[] args) {
        /* usage:
           <call> repl [<file>], starts a repl after having loaded file
           <call> batch <file>, runs file in batch mode, no repl is touched
         */
        if(hasFile(args)) {
            return new RunningParameters()
                .setRunningMode(argsRunningMode(args))
                .setFilename(argsFilename(args));
        }
        else {
            return new RunningParameters()
                .setRunningMode(argsRunningMode(args));
        }
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

    private RunningParameters() {}
    private RunningParameters setFilename(String filename) {
        this.withFile = true;
        this.filename = filename;
        return this;
    }
    private RunningParameters setRunningMode(RunningMode rn) {
        this.runningMode = rn;
        return this;
    }

    private static boolean hasFile(String[] args) {
        if (args.length == 0)
            return false;
        if(specifiesRunningMode(args[0]))
            return args.length >= 2;
        return true;
    }

    private static String argsFilename(String[] args) {
        if(specifiesRunningMode(args[0]))
            return args[1];
        return args[0];
    }

    private static boolean specifiesRunningMode(String s) {
        return s.equals("repl") || s.equals("batch");
    }

    private static RunningMode argsRunningMode(String[] args) {
        if (args.length == 0)
            return RunningMode.REPL_MODE;
        return args[0].equals("repl") ? RunningMode.REPL_MODE
            : RunningMode.BATCH_MODE;
    }
}
