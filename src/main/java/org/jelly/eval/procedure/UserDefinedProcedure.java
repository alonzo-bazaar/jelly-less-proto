package org.jelly.eval.procedure;

import java.util.List;

import org.jelly.eval.runtime.Environment;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.lang.data.LispSymbol;

public class UserDefinedProcedure implements Procedure {
    // TODO to be refined into a LambdaList class
    // to also allows optional and/or keyword arguments
    private final Environment definitionEnvironment;
    private final LambdaList formalParameters;
    private final SequenceEvaluable functionBody;

    public UserDefinedProcedure(Environment definitionEnvironment,
                                List<LispSymbol> formalParameters,
                                SequenceEvaluable functionBody) {
        this.definitionEnvironment = definitionEnvironment;
        this.formalParameters = LambdaList.fromList(formalParameters);
        this.functionBody = functionBody;
    }

    /**
     * @params values the values of the arguments with which the procedure is called
     */
    @Override
    public Object call(List<Object> values) {
        return functionBody.eval(definitionEnvironment.extend(formalParameters.bind(values)));
    }
}
