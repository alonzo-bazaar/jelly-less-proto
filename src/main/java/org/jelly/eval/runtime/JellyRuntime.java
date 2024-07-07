package org.jelly.eval.runtime;

import java.io.File;
import java.io.FileNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jelly.eval.builtinfuns.*;
import org.jelly.eval.environment.Environment;
import org.jelly.eval.errors.IncorrectArgumentListException;
import org.jelly.eval.errors.IncorrectTypeException;
import org.jelly.eval.runtime.error.JellyError;
import org.jelly.lang.data.*;
import org.jelly.lang.errors.CompilationError;

import org.jelly.eval.evaluable.procedure.Procedure;
import org.jelly.eval.evaluable.compile.Compiler;
import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.environment.errors.VariableDoesNotExistException;

import org.jelly.parse.syntaxtree.SyntaxTreeIterator;
import org.jelly.parse.token.TokenIterator;

import org.jelly.utils.FileLineIterator;
import org.jelly.utils.DebuggingUtils;

import org.jelly.eval.utils.FileSystemUtils;
import org.jelly.utils.LispLists;
import org.jelly.utils.OsUtils;


public class JellyRuntime {
    // outside interface to the underlying "lisp machine"
    /*
     * java code will use the Runtime object to call lisp code and lisp functionalities
     * and it will also use a Runtime methods to fetch the results
     * Runtime is not a singleton, multiple lisp Runtime objects can coexist,
     * representing different lisp "sessions"
     */
    private Path cwd = Paths.get(System.getProperty("user.dir"));
    private final Environment env = buildInitialEnvironment();

    public JellyRuntime() {
        loadStandardLibrary();
    }

    private void loadStandardLibrary () {
        Path home = Paths.get(System.getProperty("user.home"));
        // TODO magari falla funzionare un po' meglio
        // TODO comunque non si installa in automatico sto file, quindi eh...
        Path stdLibPath = switch(OsUtils.getOs()) {
            case OsUtils.OSType.Linux, OsUtils.OSType.MacOS
                    -> home.resolve(".local/lib/jelly/stdlib.scm");
            case OsUtils.OSType.Windows
                    -> home.resolve("\\AppData\\Roaming\\jelly\\stdlib.scm");
            case OsUtils.OSType.Other -> {
                System.err.println("Unknown os type " + System.getProperty("os.name", "generic"));
                System.err.println("Assuming it's posix, so loading posix stdlib path");

                yield home.resolve(".local/lib/jelly/stdlib.scm");
            }
        };

        try {
            evalFile(stdLibPath.toFile());
        } catch(FileNotFoundException e) {
            System.err.println("could not find standard library file \"" + stdLibPath + "\"  some functionality may be missing");
        }
    }

    public Object evalExpr(Object le) throws CompilationError {
        Evaluable ev = Compiler.compileExpression(le);
        return ev.eval(env);
    }

    public Object evalString(String s) throws CompilationError {
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

    public Object evalFile(File f) throws FileNotFoundException, CompilationError , InvalidParameterException {
        if(f.isDirectory()) {
            throw new InvalidParameterException("Cannot evaluate directory, please provide a file");
        }

        Path oldCwd = cwd;
        cwd = FileSystemUtils.fileDir(f);
        try {
            Iterator<Object> expressions = new SyntaxTreeIterator(new TokenIterator(new FileLineIterator(f)));

            while (expressions.hasNext()) {
                Object expr = expressions.next();
                if (expressions.hasNext())
                    this.evalExpr(expr);
                else
                    return this.evalExpr(expr);
            }
        } catch(Throwable ignored) {}
        finally {
            cwd = oldCwd;
        }
        // (si spera) unreachable
        return null;
    }

    public Path resolvePath(String path) {
        Path res = cwd;
        for(String sub : path.split("/")) {
            res = res.resolve(sub);
        }
        return res;
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

    public Object call(String funName, Object... args) {
        List<Object> arglist = Arrays.stream(args).toList();
        Object o = get(funName);
        if(o instanceof Procedure p) {
            return p.call(arglist);
        }
        throw new InvalidParameterException(funName + " is not a function, and thus cannot be called");
    }

    public Environment buildInitialEnvironment() {
        Environment env = new Environment();
        /* il funzionamento di tutte questa funzioni dipende un po' tanto
         * dal fatto che env sia anche l'environment in cui verranno effettivamente
         * chiamate.
         * per quanto l'assunzione sia abbastanza sensata
         * (al momento non ci sono environment multipli)
         * mi preme di specificarla, in caso esploda qualcosa a causa di essa
         * (poi magari se implemento i package lo faccio con environment multipli, quindi non si sa mai)
         *
         * ora che funcall evaluable chiama evlist di suo non c'è bisogno di farlo qui
         * le funzioni saranno chiamte sui valori, e non sugli argomenti (simboli et al)
         */

        env.define(new LispSymbol("nil"), Constants.NIL);
        env.define(new LispSymbol("#t"), Constants.TRUE);
        env.define(new LispSymbol("#f"), Constants.FALSE);

        env.define(new LispSymbol("not"), (Procedure) values -> {
                Utils.ensureSizeExactly("not",1,values);
                return values.getFirst() == Constants.FALSE;
        });

        env.define(new LispSymbol("null?"), (Procedure) values -> {
                Utils.ensureSizeExactly("null? check",1,values);
                return values.getFirst() == Constants.NIL;
        });

        env.define(new LispSymbol("cons?"), (Procedure) values -> {
            Utils.ensureSizeExactly("cons? check",1,values);
            return values.getFirst() instanceof Cons;
        });

        env.define(new LispSymbol("list?"), (Procedure) values -> {
            Utils.ensureSizeExactly("cons? check",1,values);
            return values.getFirst() instanceof LispList;
        });

        env.define(new LispSymbol("number?"), (Procedure) values -> {
            Utils.ensureSizeExactly("cons? check",1,values);
            return values.getFirst() instanceof Number;
        });

        env.define(new LispSymbol("integer?"), (Procedure) values -> {
            Utils.ensureSizeExactly("cons? check",1,values);
            return values.getFirst() instanceof Integer;
        });

        env.define(new LispSymbol("double?"), (Procedure) values -> {
            Utils.ensureSizeExactly("cons? check",1,values);
            return values.getFirst() instanceof Double;
        });

        env.define(new LispSymbol("equal?"), (Procedure) values -> {
                Utils.ensureSizeExactly("equal? check",2,values);
                return values.getFirst().equals(values.get(1));
        });

        // env.define(new LispSymbol("car"), (Procedure) ListProcessing::car);
        // env.define(new LispSymbol("cdr"), (Procedure) ListProcessing::cdr);

        env.define(new LispSymbol("set-car!"), (Procedure) ListProcessing::setCar);
        env.define(new LispSymbol("set-cdr!"), (Procedure) ListProcessing::setCdr);

        // da mettere nella stdlib quando aggiungo i costruttori
        env.define(new LispSymbol("cons"), (Procedure) ListProcessing::cons);

        env.define(new LispSymbol("list"), (Procedure) LispLists::javaListToCons);
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

        env.define(new LispSymbol("tryCall"),(Procedure) FFI::tryCall);
        env.define(new LispSymbol("tryCallStatic"),(Procedure) FFI::tryCallStatic);

        env.define(new LispSymbol("display"), (Procedure) values -> {
                    Utils.ensureSizeExactly("display", 1, values);
                    System.out.print(values.getFirst());
                    return Constants.NIL;
        });

        env.define(new LispSymbol("loadFile"), (Procedure) values -> Files.loadFile(this, values));

        env.define(new LispSymbol("getCwd"), (Procedure) values -> {
            try {
                Utils.ensureSizeExactly("getCwd", 0, values);
                List<String> cwdList = FileSystemUtils.pathList(this.cwd);
                return LispLists.javaListToCons(cwdList.stream().map(a -> (Object)a).toList());
            } catch (Throwable ignored) {
                return new UndefinedValue();
            }
        });

        env.define(new LispSymbol("getCwdString"), (Procedure) values -> {
            try {
                Utils.ensureSizeExactly("getCwd", 0, values);
                return this.cwd.toString();
            } catch (Throwable ignored) {
                return new UndefinedValue();
            }
        });

        // TODO potresti rendere il dumpenv una struttura più ispezionabile per facilitare l'utilizzo di questa per debugging
        env.define(new LispSymbol("dumpenv"), (Procedure) values -> {
                env.dump();
                // env.dump() non ha effetti sullo stato dell'environment
                return Constants.NIL;
            });

        env.define(new LispSymbol("findClass"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("findClass", 1, args);
                Utils.ensureSingleOfType("findClass", 0, String.class, args);
                return Class.forName((String) args.getFirst());
            } catch (ClassNotFoundException e) {
                return new UndefinedValue();
            }
        });


        env.define(new LispSymbol("classOf"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("classOf", 1, args);
                return args.getFirst().getClass();
            } catch (Throwable t) {
                return new UndefinedValue();
            }
        });

        env.define(new LispSymbol("error"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("error", 1, args);
                Utils.ensureSingleOfType("error", 0, String.class, args);
                throw new JellyError((String)args.getFirst());
            } catch(IncorrectTypeException | IncorrectArgumentListException t) {
                throw new JellyError("error occured, message not specified");
            }
        });

        return env;
    }
}
