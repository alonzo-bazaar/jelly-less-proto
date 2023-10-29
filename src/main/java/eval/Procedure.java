package eval;

import java.util.List;

import lang.LispSymbol;
import lang.LispExpression;

public class Procedure {
    private Environment definitionEnvironment;
    // TODO to be refined into a lambda list class that also allows optional and/or keyword arguments
    private List<LispSymbol> formalParameters;
    private SequenceEvaluable functionBody;

    public Procedure(Environment definitionEnvironment,
                     List<LispSymbol> formalParameters,
                     SequenceEvaluable functionBody) {
        this.definitionEnvironment = definitionEnvironment;
        this.formalParameters = formalParameters;
        this.functionBody = functionBody;
    }

    public LispExpression call(List<LispExpression> arguments) {
        return functionBody.eval(definitionEnvironment.extend(formalParameters,
                                                              arguments));
    }
}
