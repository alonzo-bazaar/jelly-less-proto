package org.jelly.eval.runtime;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Iterator;

import org.jelly.eval.builtinfuns.*;
import org.jelly.lang.data.LispSymbol;
import org.jelly.lang.data.Constants;
import org.jelly.lang.errors.CompilationError;

import org.jelly.eval.procedure.Procedure;
import org.jelly.eval.evaluable.compile.Compiler;
import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.runtime.errors.VariableDoesNotExistException;

import org.jelly.parse.syntaxtree.SyntaxTreeIterator;
import org.jelly.parse.token.TokenIterator;

import org.jelly.utils.FileLineIterator;
import org.jelly.utils.DebuggingUtils;


public class JellyRuntime {
    // outside interface to the underlying "lisp machine"
    /*
     * java code will use the a Runtime object to call lisp code and lisp functionalities
     * and it will also use a Runtime methods to fetch the results
     * Runtime is not a singleton, multiple lisp Runtime objects can coexist,
     * representing different lisp "sessions"
     */
    private final Environment env = buildInitialEnvironment();

    public Object evalExpr(Object le) throws CompilationError {
        Evaluable ev = Compiler.compileExpression(le);
        return ev.eval(env);
    }

    public Object evalString(String s) throws CompilationError, MalformedFormException {
        Iterator<Object> ei = DebuggingUtils.expressionsFromStrings(s);
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

    public Object evalFile(File f) throws FileNotFoundException, CompilationError {
        Iterator<Object> expressions =
            new SyntaxTreeIterator
            (new TokenIterator
             (new FileLineIterator(f)));

        while(expressions.hasNext()) {
            Object expr = expressions.next();
            if (expressions.hasNext())
                this.evalExpr(expr);
            else
                return this.evalExpr(expr);
        }
        // (si spera) unreachable
        return null;
    }

    public Object get(String name) {
        return env.lookup(new LispSymbol(name));
    }

    public void set(String name, Object value) {
        try {
            env.set(new LispSymbol(name), value);
        } catch(VariableDoesNotExistException noSuch) {
            throw new RuntimeException("cannot set " + name + ", no such variable in the environment", noSuch);
        }
    }

    public void define(String name, Object value) {
        env.define(new LispSymbol(name), value);
    }

    public Environment buildInitialEnvironment() {
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
                Utils.ensureSizeExactly("not",1,values);
                return values.get(0) == Constants.FALSE;
            });

        env.define(new LispSymbol("null?"), (Procedure) values -> {
                Utils.ensureSizeExactly("null",1,values);
                return values.get(0) == Constants.NIL;
            });

        env.define(new LispSymbol("equal?"), (Procedure) values -> {
                Utils.ensureSizeExactly("equal?",2,values);
                return values.get(0).equals(values.get(1));
            });

        env.define(new LispSymbol("car"), (Procedure) ListProcessing::car);
        env.define(new LispSymbol("cdr"), (Procedure) ListProcessing::cdr);
        env.define(new LispSymbol("cons"), (Procedure) ListProcessing::cons);

        env.define(new LispSymbol("list"), (Procedure) Utils::javaListToCons);
        env.define(new LispSymbol("+"), (Procedure) Arith::sum);
        env.define(new LispSymbol("-"), (Procedure) Arith::diff);
        env.define(new LispSymbol("*"), (Procedure) Arith::prod);
        env.define(new LispSymbol("/"),(Procedure) Arith::ratio);
        env.define(new LispSymbol("mod"),(Procedure) Arith::modulo);
        env.define(new LispSymbol(">"),(Procedure) Arith::greaterThan);
        env.define(new LispSymbol("<"),(Procedure) Arith::lessThan);
        env.define(new LispSymbol("<="),(Procedure) Arith::lessEqual);
        env.define(new LispSymbol(">="),(Procedure) Arith::greaterEqual);
        env.define(new LispSymbol("="),(Procedure) Arith::equalTo);
        env.define(new LispSymbol("!="),(Procedure) Arith::notEqualTo);

        env.define(new LispSymbol("call"),(Procedure) FFI::call);
        env.define(new LispSymbol("callStatic"),(Procedure) FFI::callStatic);

        env.define(new LispSymbol("print"), (Procedure) values -> {
                // fatto per interagire un po' da subito
                Utils.printList(values, "");
                return Constants.NIL;
            });

        env.define(new LispSymbol("println"), (Procedure) values -> {
                // fatto per interagire un po' da subito
                Utils.printList(values, "");
                System.out.println();
                return Constants.NIL;
            });

        env.define(new LispSymbol("printty"), (Procedure) values -> {
                // fatto per interagire un po' da subito
                Utils.printListWithTypes(values, ", ");
                return Constants.NIL;
            });

        env.define(new LispSymbol("printtyln"), (Procedure) values -> {
                // fatto per interagire un po' da subito
                Utils.printListWithTypes(values, ", ");
                System.out.println();
                return Constants.NIL;
            });

        env.define(new LispSymbol("loadfile"), (Procedure) values -> Files.loadFile(this, values));
        env.define(new LispSymbol("getcwd"), (Procedure) Files::getWorkingDirectory);

        // principalmente per debugging
        // al momento lo stato del runtime non è molto ispezionabile
        // quindi per debuggare un po' meglio eh... eccolo
        env.define(new LispSymbol("dumpenv"), (Procedure) values -> {
                env.dump();
                // env.dump() non ha effetti sullo stato dell'environment
                return Constants.NIL;
            });

        return env;
    }
}
