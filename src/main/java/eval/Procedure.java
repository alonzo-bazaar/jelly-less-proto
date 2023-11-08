package eval;

import java.util.List;

import lang.LispSymbol;

public class Procedure {
    private final Environment definitionEnvironment;
    // TODO to be refined into a lambda list class that also allows optional and/or keyword arguments
    private final List<LispSymbol> formalParameters;
    private final SequenceEvaluable functionBody;

    public Procedure(Environment definitionEnvironment,
                     List<LispSymbol> formalParameters,
                     SequenceEvaluable functionBody) {
        this.definitionEnvironment = definitionEnvironment;
        this.formalParameters = formalParameters;
        this.functionBody = functionBody;
    }

    public Object call(List<Object> arguments) {
        return functionBody.eval(definitionEnvironment.extend(formalParameters,
                                                              arguments));
    }
}
