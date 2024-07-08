package org.jelly.examples;

import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.LispList;

import java.io.File;
import java.io.FileNotFoundException;

public class ScriptingExample {
    public static void main(String[] args) {
        JellyRuntime jr = new JellyRuntime();
        try {
            File file = new File("src/main/java/org/jelly/examples/process.scm");
            System.out.println(file.getAbsolutePath());
            jr.evalFile(file);
            AppData data = produceData();
            int processed = (int)jr.call("processData", data);
            useProcessedData(processed);
        } catch(FileNotFoundException notFound) {
            System.out.println("could not find file");
        }

    }
    static AppData produceData() {
        return new AppData(10, 20);
    }

    static void useProcessedData(int x) {
        // System.out.println("using data " + pd.getW() + ":" + pd.getZ());
        System.out.println(x);
    }
}