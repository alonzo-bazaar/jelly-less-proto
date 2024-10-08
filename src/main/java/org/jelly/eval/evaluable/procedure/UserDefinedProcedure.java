package org.jelly.eval.evaluable.procedure;

import java.util.List;

import org.jelly.eval.environment.Environment;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.lang.data.Symbol;

public class UserDefinedProcedure implements Procedure {
    // TODO to be refined into a LambdaList class
    // to also allows optional and/or keyword arguments
    private final Environment definitionEnvironment;
    private final LambdaList formalParameters;
    private final SequenceEvaluable functionBody;

    public UserDefinedProcedure(Environment definitionEnvironment,
                                List<Symbol> formalParameters,
                                SequenceEvaluable functionBody) {
        this.definitionEnvironment = definitionEnvironment;
        this.formalParameters = LambdaList.fromList(formalParameters);
        this.functionBody = functionBody;
    }

    /**
     * @param values: the values of the arguments with which the procedure is called
     */
    @Override
    public Object apply(List<Object> values) {
        return functionBody.eval(definitionEnvironment.extend(formalParameters.bind(values)));
    }
}
