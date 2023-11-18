package eval;

import lang.Cons;
import lang.LispList;
import lang.LispSymbol;
import lang.Constants;

import parse.ExpressionIterator;
import parse.ParsingException;
import parse.UnbalancedParensException;

import java.util.List;

public class Machine {
    // interface to the underlying "lisp machine"
    /*
     * java code will use the a Machine object to call lisp code
     * and fetch the results
     */
    private final Environment env = buildInitialEnvironment();

    public Object evalString(String s) throws ParsingException {
        ExpressionIterator ei = ExpressionIterator.fromString(s);
        while (ei.hasNext()) {
            Object le = ei.next();
            if (ei.hasNext())
                this.evalExpr(le);
            else
                return this.evalExpr(le);
        }
        // si spera unreachable
        return null;
    }

    public Object evalExpr(Object le) {
        Evaluable ev = EvaluableCreator.fromExpression(le);
        return ev.eval(env);
    }

    public Object evalFile(String s) {
        return null;
    }

    public Object get(String name) {
        return null;
    }

    public void set(String name, Object value) {
        return;
    }

    public void reset() {
        return;
    }

    // boh
    Object lispEvalString(String s) {
        return null;
    }

    Object lispEvalFile(String s) {
        return null;
    }

    Object extractObjectFromExpression(Object le) {
        return null;
    }

    static Environment buildInitialEnvironment() {
        try {
            Environment env = new Environment();
            /* il funzionamento di tutte questa funzioni dipende un po' tanto
             * dal fatto che env sia anche l'environment in cui verranno effettivamente
             * chiamate.
             * per quanto l'assunzione sia abbastanza sensata
             * (al momento non ci sono environment multipli)
             * mi preme di specificarla, in caso esploda qualcosa a causa di questa
             *
             * ora che funcall evaluable chiama evlist di suo non c'è bisogno di farlo qui
             * le funzioni saranno chiamte sui valori, e non sugli argomenti (simboli et al)
             */

            env.define(new LispSymbol("car"), (Procedure) values -> {
                ArgUtils.throwIfNotExactSize("car", 1, values);
                ArgUtils.throwIfNotLispList("car", values.get(0));
                return ((LispList) (values.get(0))).getCar();
            });

            env.define(new LispSymbol("cdr"), (Procedure) values -> {
                ArgUtils.throwIfNotExactSize("cdr", 1, values);
                ArgUtils.throwIfNotLispList("cdr", values.get(0));
                return ((LispList) (values.get(0))).getCdr();
            });

            env.define(new LispSymbol("cons"), (Procedure) values -> {
                ArgUtils.throwIfNotExactSize("cons", 2, values);
                return new Cons(values.get(0), values.get(1));
            });


            env.define(new LispSymbol("not"), (Procedure) values -> {
                    ArgUtils.throwIfNotExactSize("not",1,values);
                    return Utils.isFalsey(values.get(0));
                });

            env.define(new LispSymbol("null"), (Procedure) values -> {
                    ArgUtils.throwIfNotExactSize("null",1,values);
                    return values.get(0) == Constants.NIL;
                });

            env.define(new LispSymbol("+"), (Procedure) ArgArith::sum);

            env.define(new LispSymbol("-"), (Procedure) ArgArith::diff);

            env.define(new LispSymbol("*"), (Procedure) ArgArith::prod);

            env.define(new LispSymbol("/"),(Procedure) ArgArith::ratio);

            env.define(new LispSymbol(">"),(Procedure) ArgArith::greaterThan);

            env.define(new LispSymbol("<"),(Procedure) ArgArith::lessThan);

            env.define(new LispSymbol("<="),(Procedure) ArgArith::lessEqual);

            env.define(new LispSymbol(">="),(Procedure) ArgArith::greaterEqual);
            
            env.define(new LispSymbol("="),(Procedure) ArgArith::equalTo);

            env.define(new LispSymbol("!="),(Procedure) ArgArith::notEqualTo);

            env.define(new LispSymbol("print"), (Procedure) values -> {
                    // fatto per interagire un po' da subito
                    ArgUtils.printList(values);
                    return Constants.NIL;
                });

            return env;
        } catch (EnvironmentException e) {
            System.out.println("Error while generating the environment of type : " + e.getClass().getCanonicalName());
            System.out.println(e.getMessage());
            System.out.println("environment will probably not work");
            System.out.println("returning a null to fail as quickly as possible");
            System.out.println("whatever happened here is not supposed to continue");

            return null;
        }
    }
}
