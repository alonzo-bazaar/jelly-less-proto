package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.*;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.*;
import org.jelly.parse.errors.SyntaxTreeParsingException;

// factory, circa
public class Compiler {
    public static Evaluable compileExpression(Object expr)
        throws SyntaxTreeParsingException {
        try {
            return switch(expr) {
                case null -> throw new SyntaxTreeParsingException("null object cannot be part of an expression");
                case Cons c -> {
                    FormCompiler form = compilerForForm(c);
                    form.check();
                    yield form.compile();
                }
                default -> fromAtom(expr);
            };
        } catch(MalformedFormException mfe) {
            // public facing methods should not throw low level checked expressions,
            // translate potential MalformedExpression into a runtime exception
            throw new SyntaxTreeParsingException(mfe.getMessage(), mfe);
        }
    }

    private static FormCompiler compilerForForm(Cons c) {
        if (c.getCar() instanceof Symbol sym) {
            // is it a special form?
            return switch(sym.name()) {
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
                case "define-library" -> new LibraryDefinitionCompiler(c);
                case "import" -> new ImportCompiler(c);

                // altrimenti è una chiamata a funzione
                default -> new FuncallFormCompiler(c);
            };
        }
        // altrimenti è una chiamata a funzione (x2)
        return new FuncallFormCompiler(c);
    }

    static void checkExpression(Object o) throws MalformedFormException {
        switch(o) {
            // first of all ensure not null
            case null -> throw new MalformedFormException("null form");

            // check for lists (nil or cons)
            case Nil n -> {
                if(n!=Constants.NIL)
                    throw new MalformedFormException("no nil value except for NIL can be used");
                // else ok
            }
            case Cons c -> checkCons(c);

            // if not null or list it's a non-null atom, we're ok with all non-null atoms
            default -> {return;}
        }
    }

    private static void checkCons(Cons c) throws MalformedFormException {
        compilerForForm(c).check();
    }

    private static Evaluable fromAtom(Object exp) {
        if (exp instanceof Symbol sym)
            return new LookupEvaluable(sym);
        else
            return new ConstantEvaluable(exp);
    }
}
