package org.jelly.eval.evaluable;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.library.ImportSet;
import org.jelly.lang.data.Undefined;

import java.util.List;

public class ImportEvaluable implements Evaluable {
    private final ImportSet importSet;

    public ImportEvaluable(ImportSet importSet) {
        this.importSet = importSet;
    }

    @Override
    public Object eval(Environment env) {
        importSet.importInto(env);
        return importSet;
    }
}
