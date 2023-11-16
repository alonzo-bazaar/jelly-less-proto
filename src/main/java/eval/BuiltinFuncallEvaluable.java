package eval;

import java.security.InvalidParameterException;
import java.util.List;

import java.util.Optional;

import lang.LispSymbol;
import lang.Constants;
import lang.LispList;
import lang.Cons;
import lang.Arith;

public class BuiltinFuncallEvaluable implements Evaluable {
    private final LispSymbol funSym;
    private final List<Evaluable> uncomputed_args;

    public BuiltinFuncallEvaluable(LispSymbol funSym, List<Evaluable> args) {
        this.funSym = funSym;
        this.uncomputed_args = args;
    }

    @Override
    public Object eval(Environment env) {
        return this.call(env);
    }

    // TODO : this list is overly coupled with the call() function
    private final static String[] builtinFunNames = {
            "cons", "car", "cdr",
            "not", "null",
            "+", "-", "*", "/", ">", "<", "=", "<=", ">=", "!=",
            "print"
    };
    public static boolean isBuiltinFunctionName(LispSymbol sym) {
        for(String s : builtinFunNames) {
            if (sym.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    Object call(Environment env) throws InvalidParameterException {
        String funName = funSym.getName();
        List<Object> computed_args = uncomputed_args.stream().map(exp -> exp.eval(env)).toList();
        return null;
    }

}
