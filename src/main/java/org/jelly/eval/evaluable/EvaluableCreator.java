package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.Utils;
import org.jelly.lang.data.*;
import org.jelly.parse.errors.SynthaxTreeParsingException;
import org.jetbrains.annotations.NotNull;

// non l'ho chiamato factory visto che non somiglia molto al pattern
// (e visto che non ci stava neanche troppo il pattern)
public class EvaluableCreator {
    public static Evaluable fromExpression(Object le)
        throws SynthaxTreeParsingException {
        try {
            if (le instanceof Cons c) {
                checkCons(c);
                return fromCheckedList(c);
            } else {
                // all atoms are valid, no need to check atoms
                return FormBuilding.fromAtom(le);
            }
        } catch(MalformedFormException mfe) {
            // malformed form exception STAYS IN THE PARSER
            // no checked exceptions from internal code go outside
            // NO EXCEPTIONS
            throw new SynthaxTreeParsingException(mfe.getMessage(), mfe);
        }
    }

    // crea i sotto-evaluable necessari e ci costruisce l'Evaluable mposto
    public static Evaluable fromCheckedList(Cons c) throws SynthaxTreeParsingException, MalformedFormException {
        if (c.getCar() instanceof LispSymbol sym) {
            // is it a special form?
            return switch(sym.getName()) {
            case "quote" -> new ConstantEvaluable(c.nth(1));
            case "if" -> IfForm.fromCheckedAST(c);
            case "when" -> whenFromCons(c);
            case "unless" -> unlessFromCons(c);
            case "while" -> WhileForm.fromCheckedAST(c);
            case "do" -> DoForm.fromCheckedAST(c);
            case "let" -> LetForm.fromCheckedAST(c);
            case "lambda" -> lambdaFromCons(c);
            case "define" -> definitionFromCons(c);
            case "set!" -> setFromCons(c);
            case "begin" -> sequenceFromCons(c);
            case "and" -> andFromCons(c);
            case "or" -> orFromCons(c);

            /* altrimenti è una chiamata a funzione
             * NOTA: qui fromExpression del car visto che il car potrebbe essere
             * sia un simbolo (e quindi si cerca la funzione associata)
             * (se si trova un simbolo fromExpression ritorna una LookupEvaluation)
             * che una lambda (e quindi si chiama la funzione descritta)
             * (se si trova una lambda fromExpression ritorna una lambdaEvaluation)
             */
            default -> new FuncallEvaluable(fromExpression(c.getCar()), Utils.toJavaList((LispList)c.getCdr()));
            };
        }
        // stesso ragionamento del fromExpression car
        return new FuncallEvaluable(fromExpression(c.getCar()), Utils.toJavaList((LispList)c.getCdr()));
    }

    public static void ensureForm(Object o) throws MalformedFormException {
        switch(o) {
            // first of all ensure not null
            case null -> throw new MalformedFormException("null form");

            // check for lists (nil or cons)
            case NilValue n -> {
                if(n!=Constants.NIL)
                    throw new MalformedFormException("no nil value except for NIL can be used");
                // else ok
            }
            case Cons c -> checkCons(c);

            // if not null or list it's a non null atom, we're ok with all non null atoms
            default -> {return;}
        }
    }

    public static void checkCons(Cons c) throws MalformedFormException {
        if(c.getCar() instanceof LispSymbol ls) {
            switch(ls.getName()) {
                case "quote" -> {return;} // quoted expressions just contain data, no syntax checking needed
                case "if" -> IfForm.checkAST(c);
                case "when" -> ensureWhenForm(c);
                case "unless" -> ensureUnlessForm(c);
                case "let" -> LetForm.checkAST(c);
                case "lambda" -> ensureLambdaForm(c);
                case "do" -> DoForm.checkAST(c);
                case "while" -> WhileForm.checkAST(c);
                case "set" -> ensureSetForm(c);
                case "define" -> ensureDefinitionForm(c);
                case "and" -> ensureAndForm(c);
                case "or" -> ensureOrForm(c);
            }
        }
        /* "if a code list, it's not a lambda application, and it doesn't start with a symbol then it's incorrect"
         * says the man who completely forgot you can return functions from this shit
         * also all function calls are grammatically correct, incorrect parameter lists are checked at runtime because
         * the functions are created at runtime (this interpreter has great static checking btw)
         */
    }

    // poi unlessForm extends IfForm e WhenForm extends IfForm
    @NotNull
    private static IfForm unlessFromCons(Cons c) throws MalformedFormException {
        ensureUnlessForm(c);
        return unlessFromEnsuredCons(c);
    }

    private static void ensureUnlessForm(Cons c) throws MalformedFormException {
        FormBuilding.verifyFlatFixed(c, "unless", new String[]{"condition", "alternative"});
    }

    public static IfForm unlessFromEnsuredCons(Cons c) {
        return new IfForm(fromExpression(c.nth(1)),
                               new ConstantEvaluable(Constants.FALSE),
                               fromExpression(c.nth(2)));
    }

    @NotNull
    private static IfForm whenFromCons(Cons c) throws MalformedFormException {
        ensureWhenForm(c);
        return whenFromEnsuredCons(c);
    }

    private static void ensureWhenForm(Cons c) throws MalformedFormException {
        FormBuilding.verifyFlatFixed(c, "when", new String[]{"condition", "consequent"});
    }

    public static IfForm whenFromEnsuredCons(Cons c) {
        return new IfForm(fromExpression(c.nth(1)),
                               fromExpression(c.nth(2)),
                               new ConstantEvaluable(Constants.FALSE));
    }

    @NotNull
    private static UserDefinedLambdaEvaluable lambdaFromCons(Cons c) throws MalformedFormException {
        ensureLambdaForm(c);
        return lambdaFromEnsuredCons(c);
    }

    private static void ensureLambdaForm(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c,"lambda"))
            throw new RuntimeException("lambda lambda lambda sosteneva tesi e illusioni");
        if(c.length() < 2)
            throw new MalformedFormException("lambda form must have at least a parameter list");
        try {
            ensureLambdaList(LispLists.requireList(c.nth(1)));
            FormBuilding.ensureSequenceList(LispLists.requireList(c.nthCdr(2)));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("lambda form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("lambda form is malformed, either too short or parameter list is not a list", cce);
        }
    }

    private static void ensureLambdaList(LispList ll) throws MalformedFormException {
        List<Object> l = Utils.toJavaList(ll);
        for(Object o : l) {
            if(!(o instanceof LispSymbol)) {
                throw new MalformedFormException(o + " should not be in a lambda list, only symbols can be there");
            }
        }
    }

    @NotNull
    private static UserDefinedLambdaEvaluable lambdaFromEnsuredCons(Cons c) {
        LispList formalParameters = LispLists.requireList(c.nth(1));
        LispList body = LispLists.requireList(c.nthCdr(2));
        List<LispSymbol> paramsList = Utils.toStream(formalParameters)
                .map(a -> (LispSymbol)a)
                .toList();
        SequenceEvaluable bodEval = FormBuilding.sequenceFromConsList(body);
        return new UserDefinedLambdaEvaluable(paramsList, bodEval);
    }

    @NotNull
    private static AndEvaluable andFromCons(Cons c) throws MalformedFormException {
        ensureAndForm(c);
        return andFromEnsuredCons(c);
    }

    private static void ensureAndForm(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c, "and"))
            throw new RuntimeException("ok, and?");
        try {
            FormBuilding.ensureSequenceList(LispLists.requireList(c.getCdr()));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("and form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("and form is malformed, given parameters are not a list", cce);
        }
    }

    private static AndEvaluable andFromEnsuredCons(Cons c) throws MalformedFormException {
        return new AndEvaluable(FormBuilding.toEvaluableList(LispLists.requireList(c.getCdr())));
    }

    @NotNull
    private static OrEvaluable orFromCons(Cons c) throws MalformedFormException {
        ensureOrForm(c);
        return orFromEnsuredCons(c);
    }

    private static void ensureOrForm(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c, "or"))
            throw new RuntimeException("ok, or?");
        try {
            FormBuilding.ensureSequenceList(LispLists.requireList(c.getCdr()));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("or form is malformed because child is malformed", mfe);
        } catch(ClassCastException cce) {
            throw new MalformedFormException("or form is malformed, given parameters are not a list", cce);
        }
    }

    private static OrEvaluable orFromEnsuredCons(Cons c) throws MalformedFormException {
        return new OrEvaluable(FormBuilding.toEvaluableList(LispLists.requireList(c.getCdr())));
    }

    @NotNull
    private static SetEvaluable setFromCons(Cons c) throws MalformedFormException {
        ensureSetForm(c);
        return setFromEnsuredCons(c);
    }

    private static void ensureSetForm(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c, "set!"))
            throw new RuntimeException("set no setty");
        if(c.length() != 3)
            throw new MalformedFormException("set form must have exactly two parameters (set! <var-name> <new-value>)");
        if(!(c.nth(1) instanceof LispSymbol))
            throw new MalformedFormException("set form must with valid variable name (with symbol), " + c.nth(2) + " is not a symbol");
        try {
            ensureForm(c.nth(2));
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("set form is malformed because assigned value is not a valid form", mfe);
        }
    }

    private static SetEvaluable setFromEnsuredCons(Cons c) {
        return new SetEvaluable((LispSymbol)c.nth(1), fromExpression(c.nth(2)));
    }

    @NotNull
    private static SequenceEvaluable sequenceFromCons(Cons c) throws MalformedFormException {
        ensureSequenceForm(c);
        return FormBuilding.sequenceFromConsList(LispLists.requireList(c.getCdr()));
    }

    private static void ensureSequenceForm(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c, "begin"))
            throw new RuntimeException("beeeeeeeeeeeeeeeeeeeeeee");
        FormBuilding.ensureSequenceList(LispLists.requireList(c.getCdr()));
    }

    @NotNull
    private static DefinitionEvaluable definitionFromCons(Cons c) throws MalformedFormException {
        ensureDefinitionForm(c);
        return definitionFromEnsuredCons(c);

    }

    private static void ensureDefinitionForm(Cons c) throws MalformedFormException {
        // (define var val)
        // (define (fun params) body)
        if(!FormBuilding.startsWithSym(c, "define"))
            throw new RuntimeException("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        if(c.nth(1) instanceof LispSymbol) {
            try {
                ensureForm(c.nth(2));
            } catch(MalformedFormException mfe) {
                throw new MalformedFormException("define form for symbol " + c.nth(1) + " malformed because assigned value is malformed");
            }
        }
        else if(c.nth(1) instanceof Cons fun) {
            if(!(fun.getCar() instanceof LispSymbol))
                throw new MalformedFormException("cannot define function " + fun.getCar() + " as " + fun.getCar() + " is not a valid function name (it is not a symbol)");
            ensureLambdaList(LispLists.requireList(fun.getCdr()));
            FormBuilding.ensureSequenceList(LispLists.requireList(c.nthCdr(2)));
        }
        else throw new MalformedFormException("define form is malformed, defined variable" + c.nth(1) + "is neither a symbol nor a function declaration");
    }

    private static DefinitionEvaluable definitionFromEnsuredCons(Cons c) {
        if(c.nth(1) instanceof LispSymbol definedVarName)
            return new DefinitionEvaluable(definedVarName,
                                           fromExpression(c.nth(2)));

        else {
            Cons funSpec = LispLists.requireCons(c.nth(1));
            LispSymbol funName = (LispSymbol)funSpec.getCar();
            LispList funParams = LispLists.requireList(funSpec.getCdr());
            LispList body = LispLists.requireList(c.nthCdr(2));

            List<LispSymbol> paramsList = Utils.toJavaList(funParams)
                    .stream()
                    .map(a -> (LispSymbol) a)
                    .toList();
            SequenceEvaluable bodEval = FormBuilding.sequenceFromConsList(body);

            return new DefinitionEvaluable
                    (funName,
                            new UserDefinedLambdaEvaluable(paramsList, bodEval));
        }
    }
}
