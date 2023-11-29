package eval;

import java.util.List;

import lang.LispSymbol;

public class UserDefinedProcedure implements Procedure {
    // TODO to be refined into a LambdaList class
    // to also allows optional and/or keyword arguments
    private final Environment definitionEnvironment;
    private final List<LispSymbol> formalParameters;
    private final SequenceEvaluable functionBody;

    public UserDefinedProcedure(Environment definitionEnvironment,
                                List<LispSymbol> formalParameters,
                                SequenceEvaluable functionBody) {
        this.definitionEnvironment = definitionEnvironment;
        this.formalParameters = formalParameters;
        this.functionBody = functionBody;
    }

    /**
     * @params values the values of the arguments with which the procedure is called
     */
    @Override
    public Object call(List<Object> values) {
        Object res = functionBody.eval(definitionEnvironment.extend(formalParameters, values));
        return res;
    }
}
