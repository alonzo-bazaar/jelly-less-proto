package org.jelly.eval.evaluable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.jelly.eval.evaluable.formbuild.MalformedFormException;
import org.jelly.eval.utils.Utils;
import org.jelly.lang.data.*;
import org.jelly.lang.errors.SynthaxTreeParsingException;
import org.jetbrains.annotations.NotNull;

// non l'ho chiamato factory visto che non somiglia molto al pattern
// (e visto che non ci stava neanche troppo il pattern)
public class EvaluableCreator {
    public static Evaluable fromExpression(Object le)
        throws SynthaxTreeParsingException {
        try {
            if (le instanceof Cons c) {
                return fromList(c);
            } else {
                return fromAtom(le);
            }
        } catch(MalformedFormException mfe) {
            // malformed form exception STAYS IN THE PARSER
            // no checked exceptions from internal code go outside
            // NO EXCEPTIONS
            throw new SynthaxTreeParsingException(mfe.getMessage());
        }
    }

    // crea i sotto-evaluable necessari e ci costruisce l'Evaluable mposto
    public static Evaluable fromList(Cons c) throws SynthaxTreeParsingException, MalformedFormException {
        if (c.getCar() instanceof LispSymbol sym) {
            // is it a special form?
            switch(sym.getName()) {
            case "quote":
                return new ConstantEvaluable(c.nth(1));

            case "if":
                return ifFromCons(c);
                
            case "when":
                return whenFromCons(c);

            case "unless":
                return unlessFromCons(c);

            case "while":
                return whileFromCons(c);

            case "do":
                return doFromCons(c);

            case "let":
                return letFromCons(c);

            case "lambda":
                return lambdaFromCons(c);

            case "define":
                return definitionFromCons(c);

            case "set!":
                return setFromCons(c);

            case "begin":
                return sequenceFromCons(c);

            case "and":
                return andFromCons(c);

            case "or":
                return orFromCons(c);
            }
        }

        // and if all else fails, it is a normal function call
        return new FuncallEvaluable(fromExpression(c.getCar()),
                                    Utils.toJavaList((LispList)c.getCdr()));
        /* NOTA: qui fromExpression del car visto che il car potrebbe essere
         * sia un simbolo (e quindi si cerca la funzione associata)
         * (se si trova un simbolo fromExpression ritorna una LookupEvaluation)
         * che una lambda (e quindi si chiama la funzione descritta)
         * (se si trova una lambda fromExpression ritorna una lambdaEvaluation)
         */
    }

    private static void ensureForm(Object o) throws MalformedFormException {
        switch(o) {
            // first of all ensure not null
            case null -> throw new MalformedFormException("null form");

            // check for lists (nil or cons)
            case NilValue n -> {
                if(n!=Constants.NIL)
                    throw new MalformedFormException("no nil value except for NIL can be used");
                // else ok
            }
            case Cons c -> ensureCons(c);

            // if not null or list it's a non null atom, we're ok with all non null atoms
            default -> {return;}
        }
    }

    private static void ensureCons(Cons c) throws MalformedFormException {
        if(c.getCar() instanceof LispSymbol ls) {
            switch(ls.getName()) {
                case "quote" -> {return;} // quoted expressions just contain data, no syntax checking needed
                case "if" -> ensureIfForm(c);
                case "let" -> ensureIfForm(c);
                case "lambda" -> ensureIfForm(c);
                case "do" -> ensureIfForm(c);
                case "while" -> ensureIfForm(c);
                case "set" -> ensureIfForm(c);
                case "define" -> ensureIfForm(c);
                case "and" -> ensureIfForm(c);
                case "or" -> ensureIfForm(c);
            }
        }
        else {
            /* data lists will be skipped, as everything you find in a quoted form is just going to be data,
             * no point checking for valid code, since it's not code
             */
            throw new MalformedFormException("code list starts with " + c.getCar() + ", not a symbol, can be neither special form nor function call");
        }
    }

    private static String renderFormPrototype(String formName, String[] childrenNames) {
        StringBuilder formPrototypeBuilder = new StringBuilder();
        formPrototypeBuilder.append("(").append(formName);
        Arrays.stream(childrenNames).forEach(a -> formPrototypeBuilder.append(" <").append(a).append(">"));
        return formPrototypeBuilder.append(")").toString();
    }

    private static void verifyFlatFixed(Cons form, String formName, String[] childrenNames) throws MalformedFormException {
        /*
         * encapsulates some repeated behaviour in validating certain "flat" forms
         *
         * this function works under the following assumptions
         * 1. the form has a fixed amount of children as dictated by the syntax
         * 2. the syntax only dictates the validity of the immediate children of the form, that is
         * it only cares about the fact that every element of the list starting with if, when, unless, &Co. are correct
         * this does not work for things like let, do, or lambda, as those demand the parameters to fit in a certain
         * nested structure, not in a plain "all children are good" structure
         *
         * these assumptions are common enough that it made sense to write this function
         */
        if(! startsWithSym(form, formName)) {
            throw new RuntimeException("expected form to start with " + formName + " symbol, but it starts with " + form.getCar());
        }
        if(form.length() != (childrenNames.length + 1)) {
            String formPrototype = renderFormPrototype(formName, childrenNames);
            throw new MalformedFormException("form must have exactly " + childrenNames.length + " children in the form " + formPrototype);
        }
        for(int i = 1; i<form.length(); ++i) {
            try {
                ensureForm(form.nth(i));
            } catch(MalformedFormException mfe) {
                throw new MalformedFormException(formName + " form is malformed because " + childrenNames[i-1] + " is malformed");
            }
        }
    }

    private static boolean startsWithSym(LispList ll, String s) {
        return ((LispSymbol)ll.getCar()).getName().equals(s);
    }

    @NotNull
    private static IfEvaluable ifFromCons(Cons c) throws MalformedFormException {
        ensureIfForm(c);
        return ifFromEnsuredCons(c);
    }

    private static void ensureIfForm(Cons c) throws MalformedFormException {
        verifyFlatFixed(c, "if", new String[]{"condition", "consequent", "alternative"});
    }

    private static IfEvaluable ifFromEnsuredCons(Cons c) {
        return new IfEvaluable(fromExpression(c.nth(1)), fromExpression(c.nth(2)), fromExpression(c.nth(3)));
    }

    @NotNull
    private static IfEvaluable unlessFromCons(Cons c) throws MalformedFormException {
        ensureUnlessForm(c);
        return unlessFromEnsuredCons(c);
    }

    private static void ensureUnlessForm(Cons c) throws MalformedFormException {
        verifyFlatFixed(c, "unless", new String[]{"condition", "alternative"});
    }

    public static IfEvaluable unlessFromEnsuredCons(Cons c) {
        return new IfEvaluable(fromExpression(c.nth(1)),
                               new ConstantEvaluable(Constants.FALSE),
                               fromExpression(c.nth(2)));
    }

    @NotNull
    private static IfEvaluable whenFromCons(Cons c) throws MalformedFormException {
        ensureWhenForm(c);
        return whenFromEnsuredCons(c);
    }

    private static void ensureWhenForm(Cons c) throws MalformedFormException {
        verifyFlatFixed(c, "when", new String[]{"condition", "consequent"});
    }

    public static IfEvaluable whenFromEnsuredCons(Cons c) {
        return new IfEvaluable(fromExpression(c.nth(1)),
                               fromExpression(c.nth(2)),
                               new ConstantEvaluable(Constants.FALSE));
    }

    @NotNull
    private static WhileLoopEvaluable whileFromCons(Cons c) throws MalformedFormException {
        ensureWhileForm(c);
        return whileFromEnsuredCons(c);
    }

    private static void ensureWhileForm(Cons c) throws MalformedFormException {
        if(!startsWithSym(c, "while"))
            throw new RuntimeException("expected form to start with while symbol, but it starts with " + c.getCar());
        if(c.length() < 2)
            throw new MalformedFormException("while form expected to have exactly at least two members (while <condition> <sequence-element>*");
        try {
            LispList ll = LispLists.requireList(c.getCdr());
            for(Object o : Utils.toJavaList(ll)) {
                ensureForm(o);
            }
        } catch(ClassCastException cce) {
            throw new MalformedFormException("while body is not a list, cannot turn into sequence", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("while form is malformed because body element is malformed", mfe);
        }
    }

    private static WhileLoopEvaluable whileFromEnsuredCons(Cons c) {
        return new WhileLoopEvaluable(fromExpression(c.nth(1)),
                   sequenceFromConsList((LispList) c.nthCdr(2)));
    }

    @NotNull
    private static DoEvaluable doFromCons(Cons c) {
        try {
            return _doFromCons(c);
        } catch (ClassCastException cce) {
            throw new SynthaxTreeParsingException
                    ("do form is malformed, most likely some subform has incorrect nesting, check the parentheses, the error in most likely somewhere in the variable declarations or in the stop condition\n"
                            + cce.getMessage());
        }
    }

    @NotNull
    private static DoEvaluable _doFromCons(Cons c) {
        Cons doVars = LispLists.requireCons(c.nth(1));
        Cons doStop = LispLists.requireCons(c.nth(2));
        LispList doBody = LispLists.requireList(c.nthCdr(3));

        List<LispSymbol> names = Utils.toStream(doVars)
                .map(var -> (LispSymbol)(((Cons)var).nth(0)))
                .toList();

        List<Evaluable> initForms = Utils.toStream(doVars)
                .map(var -> fromExpression(((Cons)var).nth(1)))
                .toList();

        List<Evaluable> updateForms = Utils.toStream(doVars)
                .map(var -> fromExpression(((Cons)var).nth(2)))
                .toList();

        Evaluable stopCondition = fromExpression(doStop.nth(0));
        Evaluable returnOnStop = fromExpression(doStop.nth(1));

        SequenceEvaluable body = sequenceFromConsList(doBody);

        return new DoEvaluable(names, initForms, updateForms,
                body,
                stopCondition, returnOnStop);
    }

    private static void ensureDoForm(Cons c) throws MalformedFormException {
        if(!startsWithSym(c, "do"))
            throw new RuntimeException("do do do");

        if(c.length() < 3) {
            throw new MalformedFormException("do form expects at least two arguments (do (<do-variable>*) <do-stop> <sequence-element>*)");
        }

        try {
            // vars
            ensureDoVariables(LispLists.requireList(c.nth(1)));

            // stop
            ensureDoStop(LispLists.requireList(c.nth(2)));

            // body
            List<Object> l = Utils.toJavaList(LispLists.requireList(c.nthCdr(3)));
            for(Object o : l) {
                ensureForm(o);
            }
        } catch(ClassCastException cce) {
            throw new MalformedFormException("do form is malformed, most likely some subform has incorrect nesting, check the parentheses, the error in most likely somewhere in the variable declarations or in the stop condition\n", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("do form is malformed because child is malformed", mfe);
        }
    }

    private static void ensureDoVariables(LispList ll) throws MalformedFormException {
        List<Object> allVars = Utils.toJavaList(ll);
        for(Object o : allVars) {
            List<Object> var = Utils.toJavaList(LispLists.requireCons(o));
            if(var.size() != 3)
                throw new MalformedFormException("do variable must have exactly 3 elements, (<name> <init-form> <update-form>)");
            if(!(var.getFirst() instanceof LispSymbol))
                throw new MalformedFormException("do variable binding must starts with symbol, " + var.get(0) + " is not a valid variable name");
            ensureForm(var.get(1));
            ensureForm(var.get(2));
        }
    }

    private static void ensureDoStop(LispList ll) throws MalformedFormException {
        if(ll.length() > 2) {
            throw new MalformedFormException("do stop must have less than three elements ([<stop-condition> [<stop-return]])");
        }
    }

    @NotNull
    private static LetEvaluable letFromCons(Cons c) {
        try {
            return _letFromCons(c);
        }
        catch(ClassCastException cce) {
            throw new SynthaxTreeParsingException
                    ("let form is malformed, likely some subform has incorrect nesting, check the parentheses\n"
                    + cce.getMessage());
        }
    }

    @NotNull
    private static LetEvaluable _letFromCons(Cons c) {
        Cons frames = LispLists.requireCons(c.nth(1));
        LispList body = LispLists.requireList(c.nthCdr(2));
        List<LispSymbol> names = Utils.toStream(frames)
                .map(LispLists::requireCons)
                .map(bind -> (LispSymbol)bind.nth(0))
                .toList();
        List<Evaluable> vals = Utils.toStream(frames)
                .map(LispLists::requireCons)
                .map(bind -> fromExpression(bind.nth(1)))
                .toList();
        return new LetEvaluable(names, vals, sequenceFromConsList(body));
    }

    @NotNull
    private static UserDefinedLambdaEvaluable lambdaFromCons(Cons c) {
        try {
            return _lambdaFromCons(c);
        }
        catch(ClassCastException cce) {
            throw new SynthaxTreeParsingException("lambda expression is malformed\n");
        }
    }

    @NotNull
    private static UserDefinedLambdaEvaluable _lambdaFromCons(Cons c) {
        LispList formalParameters = LispLists.requireList(c.nth(1));
        Cons body = LispLists.requireCons(c.nthCdr(2));
        List<LispSymbol> paramsList = Utils.toStream(formalParameters)
                .map(a -> (LispSymbol)a)
                .toList();
        SequenceEvaluable bodEval = sequenceFromConsList(body);
        return new UserDefinedLambdaEvaluable(paramsList, bodEval);
    }

    @NotNull
    private static AndEvaluable andFromCons(Cons c) {
        if (c.getCdr() instanceof LispList and) {
            return new AndEvaluable(toEvaluableList(and));
        }
        else throw new SynthaxTreeParsingException
                ("and statement malformed, given parameters are not a list");
    }

    @NotNull
    private static OrEvaluable orFromCons(Cons c) {
        if (c.getCdr() instanceof LispList or) {
            return new OrEvaluable(toEvaluableList(or));
        }
        else throw new SynthaxTreeParsingException
            ("or statement malformed, given parameters are not a list");
    }


    @NotNull
    private static SetEvaluable setFromCons(Cons c) {
        if(c.nth(1) instanceof LispSymbol setVarName)
            return new SetEvaluable(setVarName,
                        fromExpression(c.nth(2)));
        else
            throw new SynthaxTreeParsingException
                ("set form expects a symbol as its first argument");
    }

    @NotNull
    private static SequenceEvaluable sequenceFromCons(Cons c) {
        return sequenceFromConsList(LispLists.requireList(c.getCdr()));
    }

    @NotNull
    private static DefinitionEvaluable definitionFromCons(Cons c) {
        // piccolo problema con gli instanceof
        if(c.nth(1) instanceof LispSymbol definedVarName)
            return new DefinitionEvaluable(definedVarName,
                    fromExpression(c.nth(2)));

        else if (c.nth(1) instanceof Cons funspec
                 && funspec.getCar() instanceof LispSymbol funName
                 && funspec.getCdr() instanceof LispList funParams
                 && c.nthCdr(2) instanceof LispList funBod) {
            List<LispSymbol> paramsList = Utils.toJavaList(funParams)
                .stream()
                .map(a -> (LispSymbol) a)
                .toList();
            SequenceEvaluable bodEval = sequenceFromConsList(funBod);

            return new DefinitionEvaluable
                    (funName,
                            new UserDefinedLambdaEvaluable(paramsList, bodEval));
        }
        else
            throw new SynthaxTreeParsingException
                ("define form malformed, symbol definition or valid function specification expected");
    }

    static SequenceEvaluable sequenceFromConsList(LispList c) {
        return new SequenceEvaluable(toEvaluableList(c));
    }

    static List<Evaluable> toEvaluableList(LispList c) {
        return Utils.toStream(c)
            .map(EvaluableCreator::fromExpression)
            .toList();
    }

    public static Evaluable fromAtom(Object exp) {
        if (exp instanceof LispSymbol sym)
            return new LookupEvaluable(sym);
        else
            return new ConstantEvaluable(exp);
    }
}
