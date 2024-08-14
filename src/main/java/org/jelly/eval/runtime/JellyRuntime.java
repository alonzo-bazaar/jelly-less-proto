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
import org.jelly.utils.ConsUtils;
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
        } catch(FileNotFoundException fe){
            System.err.println("cannot load file " + f.getAbsolutePath() + ", no such file or directory");
        } catch(Throwable t) {
            System.err.println("Error occured while loading file " + f.getAbsolutePath() + ", of type " + t.getClass().getCanonicalName());
            System.err.println(t.getMessage());
            t.printStackTrace();
        }
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
        return env.lookup(new Symbol(name));
    }

    public void set(String name, Object value) {
        try {
            env.set(new Symbol(name), value);
        } catch(VariableDoesNotExistException noSuch) {
            throw new RuntimeException("cannot set " + name + ", no such variable in the environment", noSuch);
        }
    }

    public void define(String name, Object value) {
        env.define(new Symbol(name), value);
    }

    public Object call(String funName, Object... args) {
        List<Object> arglist = Arrays.stream(args).toList();
        Object o = get(funName);
        if(o instanceof Procedure p) {
            return p.apply(arglist);
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

        env.define(new Symbol("nil"), Constants.NIL);
        env.define(new Symbol("#t"), Constants.TRUE);
        env.define(new Symbol("#f"), Constants.FALSE);

        env.define(new Symbol("not"), (Procedure) values -> {
                Utils.ensureSizeExactly("not",1,values);
                return values.getFirst() == Constants.FALSE;
        });

        env.define(new Symbol("null?"), (Procedure) values -> {
                Utils.ensureSizeExactly("null? check",1,values);
                return values.getFirst() == Constants.NIL;
        });

        env.define(new Symbol("cons?"), (Procedure) values -> {
            Utils.ensureSizeExactly("cons? check",1,values);
            return values.getFirst() instanceof Cons;
        });

        env.define(new Symbol("list?"), (Procedure) values -> {
            Utils.ensureSizeExactly("list? check",1,values);
            return values.getFirst() instanceof ConsList;
        });

        env.define(new Symbol("number?"), (Procedure) values -> {
            Utils.ensureSizeExactly("number? check",1,values);
            return values.getFirst() instanceof Number;
        });

        env.define(new Symbol("integer?"), (Procedure) values -> {
            Utils.ensureSizeExactly("integer? check",1,values);
            return values.getFirst() instanceof Integer;
        });

        env.define(new Symbol("double?"), (Procedure) values -> {
            Utils.ensureSizeExactly("double? check",1,values);
            return values.getFirst() instanceof Double;
        });

        env.define(new Symbol("equal?"), (Procedure) values -> {
                Utils.ensureSizeExactly("equal? check",2,values);
                return values.getFirst().equals(values.get(1));
        });

        env.define(new Symbol("eq?"), (Procedure) values -> {
            Utils.ensureSizeExactly("eq? check",2,values);
            Object a = values.get(0);
            Object b = values.get(1);

            if(a instanceof Symbol as && b instanceof Symbol bs) {
                return as.equals(bs);
            }

            return a == b;
        });

        env.define(new Symbol("eqv?"), (Procedure) values -> {
            Utils.ensureSizeExactly("eqv? check",2,values);
            Object a = values.get(0);
            Object b = values.get(1);

            if(a instanceof Long an && b instanceof Long bn)
                return an.longValue() == bn.longValue();
            if(a instanceof Integer an && b instanceof Integer bn)
                return an.intValue() == bn.intValue();
            if(a instanceof Short an && b instanceof Short bn)
                return an.shortValue() == bn.shortValue();

            if(a instanceof Double an && b instanceof Double bn)
                return an.doubleValue() == bn.doubleValue();
            if(a instanceof Float an && b instanceof Float bn)
                return an.floatValue() == bn.floatValue();

            if(a instanceof Character an && b instanceof Character bn)
                return an.charValue() == bn.charValue();

            if(a instanceof Byte an && b instanceof Byte bn)
                return an.byteValue() == bn.byteValue();

            if(a instanceof Symbol as && b instanceof Symbol bs) {
                return as.equals(bs);
            }

            return a == b;
        });

        // special kid lines
        env.define(new Symbol("car"), (Procedure) ListProcessing::car);
        env.define(new Symbol("cdr"), (Procedure) ListProcessing::cdr);
        env.define(new Symbol("setCar!"), (Procedure) ListProcessing::setCar);
        env.define(new Symbol("setCdr!"), (Procedure) ListProcessing::setCdr);
        env.define(new Symbol("cons"), (Procedure) ListProcessing::cons);

        env.define(new Symbol("list"), (Procedure) ConsUtils::toCons);
        env.define(new Symbol("+"), (Procedure) Arith::sum);
        env.define(new Symbol("-"), (Procedure) Arith::diff);
        env.define(new Symbol("*"), (Procedure) Arith::prod);
        env.define(new Symbol("/"),(Procedure) Arith::ratio);
        env.define(new Symbol("mod"),(Procedure) Arith::modulo);
        env.define(new Symbol(">"),(Procedure) Arith::greaterThan);
        env.define(new Symbol("<"),(Procedure) Arith::lessThan);
        env.define(new Symbol("<="),(Procedure) Arith::lessEqual);
        env.define(new Symbol(">="),(Procedure) Arith::greaterEqual);
        env.define(new Symbol("="),(Procedure) Arith::equalTo);
        env.define(new Symbol("!="),(Procedure) Arith::notEqualTo);

        env.define(new Symbol("call"),(Procedure) FFI::call);
        env.define(new Symbol("callStatic"),(Procedure) FFI::callStatic);

        env.define(new Symbol("tryCall"),(Procedure) FFI::tryCall);
        env.define(new Symbol("tryCallStatic"),(Procedure) FFI::tryCallStatic);

        env.define(new Symbol("construct"), (Procedure) FFI::constuct);

        env.define(new Symbol("display"), (Procedure) values -> {
                    Utils.ensureSizeExactly("display", 1, values);
                    System.out.print(values.getFirst());
                    return Constants.NIL;
        });

        env.define(new Symbol("loadFile"), (Procedure) values -> Files.loadFile(this, values));

        env.define(new Symbol("getCwd"), (Procedure) values -> {
            try {
                Utils.ensureSizeExactly("getCwd", 0, values);
                List<String> cwdList = FileSystemUtils.pathList(this.cwd);
                return ConsUtils.toCons(cwdList.stream().map(a -> (Object)a).toList());
            } catch (Throwable ignored) {
                return new Undefined();
            }
        });

        env.define(new Symbol("getCwdString"), (Procedure) values -> {
            try {
                Utils.ensureSizeExactly("getCwd", 0, values);
                return this.cwd.toString();
            } catch (Throwable ignored) {
                return new Undefined();
            }
        });

        // TODO potresti rendere il dumpenv una struttura più ispezionabile per facilitare l'utilizzo di questa per debugging
        env.define(new Symbol("dumpenv"), (Procedure) values -> {
                env.dump();
                // env.dump() non ha effetti sullo stato dell'environment
                return Constants.NIL;
            });

        env.define(new Symbol("findClass"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("findClass", 1, args);
                Utils.ensureSingleOfType("findClass", 0, String.class, args);
                return Class.forName((String) args.getFirst());
            } catch (ClassNotFoundException e) {
                return new Undefined();
            }
        });


        env.define(new Symbol("classOf"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("classOf", 1, args);
                return args.getFirst().getClass();
            } catch (Throwable t) {
                return new Undefined();
            }
        });

        env.define(new Symbol("error"), (Procedure) args -> {
            try {
                Utils.ensureSizeAtLeast("error", 1, args);
                Utils.ensureSingleOfType("error", 0, String.class, args);
                if(args.size() == 1)
                    throw new JellyError((String)args.getFirst());
                else
                    throw new JellyError((String)args.getFirst(), args.subList(1, args.size()).toArray());
            } catch(IncorrectTypeException | IncorrectArgumentListException t) {
                throw new JellyError("error occurred, cannot throw error because message not specified", t);
            }
        });

        env.define(new Symbol("array"), (Procedure) List::toArray);
        env.define(new Symbol("javaList"), (Procedure) args -> args);
        env.define(new Symbol("values"), (Procedure) Values::new);

        env.define(new Symbol("call-with-values"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("call-with-values", 2, args);
                Utils.ensureSingleOfType("call-with-values", 0, Values.class, args);
                Utils.ensureSingleOfType("call-with-values", 1, Procedure.class, args);
                return ((Procedure)args.get(1)).apply(((Values)args.get(1)).values());
            } catch(Throwable t) {
                throw new JellyError("call-with-values call failed", t);
            }
        });

        env.define(new Symbol("apply"), (Procedure) args -> {
            try {
                Utils.ensureSizeExactly("apply", 2, args);
                Utils.ensureSingleOfType("apply", 0, Procedure.class, args);
                Utils.ensureSingleOfType("apply", 1, ConsList.class, args);
                return ((Procedure)args.get(0)).apply(ConsUtils.toList(ConsUtils.requireList(args.get(1))));
            } catch(Throwable t) {
                throw new JellyError("apply call failed", t);
            }
        });

        return env;
    }
}
