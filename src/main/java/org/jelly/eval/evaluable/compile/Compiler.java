package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.*;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.*;
import org.jelly.parse.errors.SynthaxTreeParsingException;

// non l'ho chiamato factory visto che non somiglia molto al pattern
// (e visto che non ci stava neanche troppo il pattern)
public class Compiler {
    public static Evaluable compileExpression(Object le)
        throws SynthaxTreeParsingException {
        try {
            if (le instanceof Cons c) {
                // checkCons(c);
                // return fromCheckedList(c);
                FormCompiler form = compilerForForm(c);
                form.check();
                return form.compile();
            } else {
                // all atoms are valid, no need to check atoms
                return fromAtom(le);
            }
        } catch(MalformedFormException mfe) {
            // malformed form exception STAYS IN THE PARSER
            // no checked exceptions from internal code go outside
            // NO EXCEPTIONS
            throw new SynthaxTreeParsingException(mfe.getMessage(), mfe);
        }
    }

    // crea i sotto-evaluable necessari e ci costruisce l'Evaluable mposto
    private static Evaluable fromCheckedList(Cons c) {
        if (c.getCar() instanceof LispSymbol sym) {
            // is it a special form?
            return switch(sym.getName()) {
            case "quote" -> new ConstantEvaluable(c.nth(1));
            case "if" -> IfFormCompiler.fromCheckedAST(c);
            case "when" -> whenFromCheckedAST(c);
            case "unless" -> unlessFromEnsuredAST(c);
            case "while" -> WhileFormCompiler.fromCheckedAST(c);
            case "do" -> DoFormCompiler.fromCheckedAST(c);
            case "let" -> LetFormCompiler.fromCheckedAST(c);
            case "lambda" -> LambdaFormCompiler.fromCheckedAST(c);
            case "define" -> DefinitionFormCompiler.fromCheckedAST(c);
            case "set!" -> SetFormCompiler.fromCheckedAST(c);
            case "begin" -> SequenceFormCompiler.fromCheckedAST(c);
            case "and" -> AndFormCompiler.fromCheckedAST(c);
            case "or" -> OrFormCompiler.fromCheckedAST(c);

            /* altrimenti è una chiamata a funzione
             * NOTA: qui fromExpression del car visto che il car potrebbe essere
             * sia un simbolo (e quindi si cerca la funzione associata)
             * (se si trova un simbolo fromExpression ritorna una LookupEvaluation)
             * che una lambda (e quindi si chiama la funzione descritta)
             * (se si trova una lambda fromExpression ritorna una lambdaEvaluation)
             */
            default -> FuncallFormCompiler.fromCheckedAST(c);
            };
        }
        // stesso ragionamento del fromExpression car
        return new FuncallEvaluable(compileExpression(c.getCar()), ListUtils.toJavaList((LispList)c.getCdr()));
    }

    private static FormCompiler compilerForForm(Cons c) {
        if (c.getCar() instanceof LispSymbol sym) {
            // is it a special form?
            return switch(sym.getName()) {
                case "quote" -> new ConstantFormCompiler(c);
                case "if" -> new IfFormCompiler(c);
                case "when" -> new WhenFormCompiler(c);
                case "unless" -> new UnlessFormCompiler(c);
                case "while" -> new WhileFormCompiler(c);
                case "do" -> new DoFormCompiler(c);
                case "let" -> new LetFormCompiler(c);
                case "lambda" -> new LambdaFormCompiler(c);
                case "define" -> new DefinitionFormCompiler(c);
                case "set!" -> new SetFormCompiler(c);
                case "begin" -> new SequenceFormCompiler(c);
                case "and" -> new AndFormCompiler(c);
                case "or" -> new OrFormCompiler(c);

                // altrimenti è una chiamata a funzione
                default -> new FuncallFormCompiler(c);
            };
        }
        // altrimenti è una chiamata a funzione (x2)
        return new FuncallFormCompiler(c);
    }

    public static void checkExpression(Object o) throws MalformedFormException {
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

    private static void checkCons(Cons c) throws MalformedFormException {
        if(c.getCar() instanceof LispSymbol ls) {
            switch(ls.getName()) {
                case "quote" -> {return;} // quoted expressions just contain data, no syntax checking needed
                case "if" -> IfFormCompiler.checkAST(c);
                case "when" -> whenCheckAST(c);
                case "unless" -> unlessCheckAST(c);
                case "let" -> LetFormCompiler.checkAST(c);
                case "lambda" -> LambdaFormCompiler.checkAST(c);
                case "do" -> DoFormCompiler.checkAST(c);
                case "while" -> WhileFormCompiler.checkAST(c);
                case "set" -> SetFormCompiler.checkAST(c);
                case "define" -> DefinitionFormCompiler.checkAST(c);
                case "and" -> AndFormCompiler.checkAST(c);
                case "or" -> OrFormCompiler.checkAST(c);
            }
        }
        /* "if a code list, it's not a lambda application, and it doesn't start with a symbol then it's incorrect"
         * says the man who completely forgot you can return functions from this shit
         * also all function calls are grammatically correct, incorrect parameter lists are checked at runtime because
         * the functions are created at runtime (this interpreter has great static checking btw)
         */
    }

    private static void unlessCheckAST(Cons c) throws MalformedFormException {
        Utils.checkFlatFixed(c, "unless", new String[]{"condition", "alternative"});
    }

    public static IfEvaluable unlessFromEnsuredAST(Cons c) {
        return new IfEvaluable(compileExpression(c.nth(1)),
                               new ConstantEvaluable(Constants.FALSE),
                               compileExpression(c.nth(2)));
    }

    private static void whenCheckAST(Cons c) throws MalformedFormException {
        Utils.checkFlatFixed(c, "when", new String[]{"condition", "consequent"});
    }

    public static IfEvaluable whenFromCheckedAST(Cons c) {
        return new IfEvaluable(compileExpression(c.nth(1)),
                               compileExpression(c.nth(2)),
                               new ConstantEvaluable(Constants.FALSE));
    }

    static Evaluable fromAtom(Object exp) {
        if (exp instanceof LispSymbol sym)
            return new LookupEvaluable(sym);
        else
            return new ConstantEvaluable(exp);
    }
}
