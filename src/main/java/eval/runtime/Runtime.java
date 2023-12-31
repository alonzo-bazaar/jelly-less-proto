package eval.runtime;

import eval.evaluable.Evaluable;
import eval.evaluable.EvaluableCreator;
import eval.procedure.Procedure;
import eval.runtime.errors.EnvironmentException;
import eval.utils.ArgArith;
import eval.utils.ArgListProcessing;
import eval.utils.ArgUtils;
import lang.LispSymbol;
import lang.Constants;

import parse.*;
import utils.FileCharIterator;
import lang.errors.CompilationError;

import java.io.File;
import java.io.FileNotFoundException;

public class Runtime {
    // outside interface to the underlying "lisp machine"
    /*
     * java code will use the a Runtime object to call lisp code and lisp functionalities
     * and it will also use a Runtime methods to fetch the results
     * Runtime is not a singleton, multiple lisp Runtime objects can coexist,
     * representing different lisp "sessions"
     */
    private final Environment env = buildInitialEnvironment();

    public Object evalString(String s) throws CompilationError {
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

    public Object evalExpr(Object le) throws CompilationError {
        Evaluable ev = EvaluableCreator.fromExpression(le);
        return ev.eval(env);
    }

    public Object evalFile(File f)
        throws FileNotFoundException, CompilationError  {

        ExpressionIterator ei = new ExpressionIterator
            (new TokenIterator
             (new SignificantCharsIterator
              (new FileCharIterator(f))));

        while (ei.hasNext()) {
            Object le = ei.next();
            if (ei.hasNext())
                this.evalExpr(le);
            else
                return this.evalExpr(le);
        }
        // (si spera) unreachable
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

    public static Environment buildInitialEnvironment() {
        try {
            Environment env = new Environment();
            /* il funzionamento di tutte questa funzioni dipende un po' tanto
             * dal fatto che env sia anche l'environment in cui verranno effettivamente
             * chiamate.
             * per quanto l'assunzione sia abbastanza sensata
             * (al momento non ci sono environment multipli)
             * mi preme di specificarla, in caso esploda qualcosa a causa di essa
             *
             * ora che funcall evaluable chiama evlist di suo non c'è bisogno di farlo qui
             * le funzioni saranno chiamte sui valori, e non sugli argomenti (simboli et al)
             */

            env.define(new LispSymbol("nil"), Constants.NIL);
            env.define(new LispSymbol("#t"), Constants.TRUE);
            env.define(new LispSymbol("#f"), Constants.FALSE);

            env.define(new LispSymbol("not"), (Procedure) values -> {
                    ArgUtils.throwIfNotExactSize("not",1,values);
                    return values.get(0) == Constants.FALSE;
                });

            env.define(new LispSymbol("null?"), (Procedure) values -> {
                    ArgUtils.throwIfNotExactSize("null",1,values);
                    return values.get(0) == Constants.NIL;
                });

            env.define(new LispSymbol("equal?"), (Procedure) values -> {
                    ArgUtils.throwIfNotExactSize("equal?",2,values);
                    return values.get(0).equals(values.get(1));
                });

            env.define(new LispSymbol("car"), (Procedure) ArgListProcessing::car);
            env.define(new LispSymbol("cdr"), (Procedure) ArgListProcessing::cdr);
            env.define(new LispSymbol("cons"), (Procedure) ArgListProcessing::cons);

            env.define(new LispSymbol("list"), (Procedure) ArgUtils::javaListToCons);
            env.define(new LispSymbol("+"), (Procedure) ArgArith::sum);
            env.define(new LispSymbol("-"), (Procedure) ArgArith::diff);
            env.define(new LispSymbol("*"), (Procedure) ArgArith::prod);
            env.define(new LispSymbol("/"),(Procedure) ArgArith::ratio);
            env.define(new LispSymbol("mod"),(Procedure) ArgArith::modulo);
            env.define(new LispSymbol(">"),(Procedure) ArgArith::greaterThan);
            env.define(new LispSymbol("<"),(Procedure) ArgArith::lessThan);
            env.define(new LispSymbol("<="),(Procedure) ArgArith::lessEqual);
            env.define(new LispSymbol(">="),(Procedure) ArgArith::greaterEqual);
            env.define(new LispSymbol("="),(Procedure) ArgArith::equalTo);
            env.define(new LispSymbol("!="),(Procedure) ArgArith::notEqualTo);

            env.define(new LispSymbol("print"), (Procedure) values -> {
                    // fatto per interagire un po' da subito
                    ArgUtils.printList(values, "");
                    return Constants.NIL;
                });

            env.define(new LispSymbol("println"), (Procedure) values -> {
                    // fatto per interagire un po' da subito
                    ArgUtils.printList(values, "");
                    System.out.println();
                    return Constants.NIL;
                });

            env.define(new LispSymbol("printty"), (Procedure) values -> {
                    // fatto per interagire un po' da subito
                    ArgUtils.printListWithTypes(values, ", ");
                    return Constants.NIL;
                });

            env.define(new LispSymbol("printtyln"), (Procedure) values -> {
                    // fatto per interagire un po' da subito
                    ArgUtils.printListWithTypes(values, ", ");
                    System.out.println();
                    return Constants.NIL;
                });

            // principalmente per debugging
            // al momento lo stato del runtime non è molto ispezionabile
            // quindi per debuggare un po' meglio eh... eccolo
            env.define(new LispSymbol("dumpenv"), (Procedure) values -> {
                    env.dump();
                    // env.dump() non ha effetti sullo stato dell'environment
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
