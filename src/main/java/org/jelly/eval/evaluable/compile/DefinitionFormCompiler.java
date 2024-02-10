package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.*;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispLists;
import org.jelly.lang.data.LispSymbol;
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
    public static DefinitionEvaluable fromCheckedAST(Cons c) {
        if(c.nth(1) instanceof LispSymbol definedVarName)
            return new DefinitionEvaluable(definedVarName,
                    Compiler.compileExpression(c.nth(2)));

        else {
            Cons funSpec = LispLists.requireCons(c.nth(1));
            LispSymbol funName = (LispSymbol)funSpec.getCar();
            LispList funParams = LispLists.requireList(funSpec.getCdr());
            LispList body = LispLists.requireList(c.nthCdr(2));

            List<LispSymbol> paramsList = ListUtils.toJavaList(funParams)
                    .stream()
                    .map(a -> (LispSymbol) a)
                    .toList();
            SequenceEvaluable bodEval = Utils.sequenceFromConsList(body);

            return new DefinitionEvaluable
                    (funName,
                            new UserDefinedLambdaEvaluable(paramsList, bodEval));
        }
    }

    public static void checkAST(Cons c) throws MalformedFormException {
        // (define var val)
        // (define (fun params) body)
        if(!Utils.startsWithSym(c, "define"))
            throw new RuntimeException("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        if(c.nth(1) instanceof LispSymbol) {
            try {
                Compiler.checkExpression(c.nth(2));
            } catch(MalformedFormException mfe) {
                throw new MalformedFormException("define form for symbol " + c.nth(1) + " malformed because assigned value is malformed");
            }
        }
        else if(c.nth(1) instanceof Cons fun) {
            if(!(fun.getCar() instanceof LispSymbol))
                throw new MalformedFormException("cannot define function " + fun.getCar() + " as " + fun.getCar() + " is not a valid function name (it is not a symbol)");
            LambdaFormCompiler.ensureLambdaListAST(LispLists.requireList(fun.getCdr()));
            Utils.checkSequenceList(LispLists.requireList(c.nthCdr(2)));
        }
        else throw new MalformedFormException("define form is malformed, defined variable" + c.nth(1) + "is neither a symbol nor a function declaration");
    }
}
