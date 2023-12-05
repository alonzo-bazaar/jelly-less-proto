package eval.evaluable;

import java.util.List;

import eval.utils.Utils;
import lang.*;
import lang.errors.SynthaxTreeParsingException;

// non l'ho chiamato factory visto che non somiglia molto al pattern
// (e visto che non ci stava neanche troppo il pattern)
public class EvaluableCreator {
    public static Evaluable fromExpression(Object le)
        throws SynthaxTreeParsingException {
        if (le instanceof Cons c) {
            return fromList(c);
        }
        else {
            return fromAtom(le);
        }
    }

    // crea i sotto-evaluable necessari e ci costruisce l'Evaluable mposto
    public static Evaluable fromList(Cons c) throws SynthaxTreeParsingException {
        if (c.getCar() instanceof LispSymbol sym) {
            // is it a special form?
            switch(sym.getName()) {
            case "quote":
                return new ConstantEvaluable(c.nth(1));

            case "if":
                return new IfEvaluable(fromExpression(c.nth(1)),
                                       fromExpression(c.nth(2)), 
                                       fromExpression(c.nth(3))); 

            case "when":
                // if con solo il caso true
                return new IfEvaluable(fromExpression(c.nth(1)),
                                       fromExpression(c.nth(2)), 
                                       new ConstantEvaluable(Constants.FALSE)); 

            case "unless":
                // if con solo il caso false
                return new IfEvaluable(fromExpression(c.nth(1)),
                                       new ConstantEvaluable(Constants.FALSE), 
                                       fromExpression(c.nth(2))); 

            case "while":
                return new WhileLoopEvaluable(fromExpression(c.nth(1)),
                                              sequenceFromConsList((LispList)c.nthCdr(2)));

            case "do":
                try {
                if (c.nth(1) instanceof Cons doVars &&
                    c.nth(2) instanceof Cons doStop &&
                    c.nthCdr(3) instanceof LispList doBody) {

                    List<LispSymbol> names = Utils.toJavaList(doVars)
                        .stream()
                        .map(var -> (LispSymbol)(((Cons)var).nth(0)))
                        .toList();

                    List<Evaluable> initForms = Utils.toJavaList(doVars)
                        .stream()
                        .map(var -> fromExpression(((Cons)var).nth(1)))
                        .toList();

                    List<Evaluable> updateForms = Utils.toJavaList(doVars)
                        .stream()
                        .map(var -> fromExpression(((Cons)var).nth(2)))
                        .toList();

                    Evaluable stopCondition = fromExpression(doStop.nth(0));
                    Evaluable returnOnStop = fromExpression(doStop.nth(1));

                    SequenceEvaluable body = sequenceFromConsList(doBody);

                    return new DoEvaluable(names, initForms, updateForms,
                                           body,
                                           stopCondition, returnOnStop);
                }
                else {
                    throw new SynthaxTreeParsingException("do form is malformed");
                }
            } catch (ClassCastException cce) {
                throw new SynthaxTreeParsingException
                    ("do form is malformed " +
                     "parts of some inner element have the wrong type, " +
                     "most likely an error while writing the variable behaviour or the stop condition\n" +
                     cce.getMessage());
            }
                
            case "let":
                if (c.nth(1) instanceof Cons frameDesc &&
                    c.nthCdr(2) instanceof Cons letBody) {
                    List<LispSymbol> names = Utils.toJavaList(frameDesc)
                        .stream()
                        .map(bind -> (LispSymbol)((Cons)bind).nth(0))
                        .toList();
                    List<Evaluable> vals = Utils.toJavaList(frameDesc)
                        .stream()
                        .map(bind -> fromExpression(((Cons)bind).nth(1)))
                        .toList();
                    return new LetEvaluation(names,
                                             vals,
                                             sequenceFromConsList(letBody));
                }
                else
                    throw new SynthaxTreeParsingException("let form is malformed");

            case "lambda":
                if (c.nth(1) instanceof LispList formalParameters &&
                    c.nthCdr(2) instanceof Cons lambdaBody) {
                    List<LispSymbol> paramsList = Utils.toJavaList(formalParameters)
                        .stream()
                        .map(a -> (LispSymbol)a)
                        .toList();
                    SequenceEvaluable bodEval = sequenceFromConsList(lambdaBody);
                    return new UserDefinedLambdaEvaluable(paramsList, bodEval);
                }
                else
                    throw new SynthaxTreeParsingException
                        ("lambda expression is malformed");

            case "define":
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

            case "set!":
                if(c.nth(1) instanceof LispSymbol setVarName)
                    return new SetEvaluable(setVarName,
                                            fromExpression(c.nth(2)));
                else
                    throw new SynthaxTreeParsingException
                        ("set form expects a symbol as its first argument");

            case "begin":
                return sequenceFromConsList(c);

            case "and":
                /* and e or sono short circuiting
                 * serve un trattamento particolare per permettere lo short circuit
                 * o si implementano con delle macro o va fatto questo
                 * e non credo che jelly avrà mai delle macro :(
                 */
                if (c.getCdr() instanceof LispList andll) {
                    return new AndEvaluable(toEvaluableList(andll));
                }
                else throw new SynthaxTreeParsingException
                    ("and statement malformed, given parameters are not a list");
            case "or":
                if (c.getCdr() instanceof LispList orll) {
                    return new OrEvaluable(toEvaluableList(orll));
                }
                else throw new SynthaxTreeParsingException
                    ("or statement malformed, given parameters are not a list");
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
