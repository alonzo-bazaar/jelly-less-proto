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
             */

            env.define(new LispSymbol("car"), (Procedure) args -> {
                List<Object> vals = ArgUtils.evlist(args, env);

                ArgUtils.throwIfNotExactSize("car", 1, vals);
                ArgUtils.throwIfNotLispList("car", vals.get(0));
                return ((LispList) (vals.get(0))).getCar();
            });

            env.define(new LispSymbol("cdr"), (Procedure) args -> {
                List<Object> vals = ArgUtils.evlist(args, env);

                ArgUtils.throwIfNotExactSize("cdr", 1, vals);
                ArgUtils.throwIfNotLispList("cdr", vals.get(0));
                return ((LispList) (vals.get(0))).getCdr();
            });

            env.define(new LispSymbol("cons"), (Procedure) args -> {
                ArgUtils.throwIfNotExactSize("cons", 2, args);

                List<Object> vals = ArgUtils.evlist(args, env);
                return new Cons(vals.get(0), vals.get(1));
            });


            env.define(new LispSymbol("not"), (Procedure) args -> {
                    ArgUtils.throwIfNotExactSize("not",1,args);
                    return Utils.isFalsey(ArgUtils.evlist(args,env).get(0));
                });

            env.define(new LispSymbol("null"), (Procedure) args -> {
                    ArgUtils.throwIfNotExactSize("null",1,args);
                    return ArgUtils.evlist(args,env).get(0) == Constants.NIL;
                });

            env.define(new LispSymbol("+"), (Procedure) args ->
                    ArgArith.sum(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol("*"), (Procedure) args ->
                    ArgArith.prod(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol("/"),(Procedure) args ->
                       ArgArith.ratio(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol(">"),(Procedure) args ->
                       ArgArith.greaterThan(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol("<"),(Procedure) args ->
                       ArgArith.lessThan(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol("<="),(Procedure) args ->
                       ArgArith.lessEqual(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol(">="),(Procedure) args ->
                       ArgArith.greaterEqual(ArgUtils.evlist(args,env)));
            
            env.define(new LispSymbol("="),(Procedure) args ->
                       ArgArith.equalTo(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol("!="),(Procedure) args ->
                       ArgArith.notEqualTo(ArgUtils.evlist(args,env)));

            env.define(new LispSymbol("print"), (Procedure) args -> {
                    // fatto per interagire un po' da subito
                    ArgUtils.printList(ArgUtils.evlist(args,env));
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
