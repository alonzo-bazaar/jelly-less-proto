package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.*;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.utils.ConsUtils;
import org.jelly.lang.data.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefinitionFormCompiler implements FormCompiler {
    private final Cons form;
    public DefinitionFormCompiler(Cons form) {
        this.form = form;
    }

    @Override
    public DefinitionEvaluable compile() {
        return fromCheckedAST(form);
    }

    @Override
    public void check() throws MalformedFormException {
        checkAST(form);
    }

    @NotNull
    private static DefinitionEvaluable fromCheckedAST(Cons c) {
        if(c.nth(1) instanceof Symbol definedVarName)
            return new DefinitionEvaluable(definedVarName,
                    Compiler.compileExpression(c.nth(2)));

        else {
            Cons funSpec = ConsUtils.requireCons(c.nth(1));
            Symbol funName = (Symbol)funSpec.getCar();
            ConsList funParams = ConsUtils.requireList(funSpec.getCdr());
            ConsList body = ConsUtils.requireList(c.nthCdr(2));

            List<Symbol> paramsList = ListUtils.toJavaList(funParams)
                    .stream()
                    .map(a -> (Symbol) a)
                    .toList();
            SequenceEvaluable bodEval = Utils.sequenceFromConsList(body);

            return new DefinitionEvaluable
                    (funName,
                            new UserDefinedLambdaEvaluable(paramsList, bodEval));
        }
    }

    private static void checkAST(Cons c) throws MalformedFormException {
        // (define var val)
        // (define (fun params) body)
        if(!Utils.startsWithSym(c, "define"))
            throw new RuntimeException("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        if(c.nth(1) instanceof Symbol) {
            try {
                Compiler.checkExpression(c.nth(2));
            } catch(MalformedFormException mfe) {
                throw new MalformedFormException("define form for symbol " + c.nth(1) + " malformed because assigned value is malformed");
            }
        }
        else if(c.nth(1) instanceof Cons fun) {
            if(!(fun.getCar() instanceof Symbol))
                throw new MalformedFormException("cannot define function " + fun.getCar() + " as " + fun.getCar() + " is not a valid function name (it is not a symbol)");
            Utils.ensureLambdaListAST(ConsUtils.requireList(fun.getCdr()));
            Utils.checkSequenceList(ConsUtils.requireList(c.nthCdr(2)));
        }
        else throw new MalformedFormException("define form is malformed, defined variable" + c.nth(1) + "is neither a symbol nor a function declaration");
    }
}
