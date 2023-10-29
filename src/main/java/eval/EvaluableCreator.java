package eval;

import java.security.InvalidParameterException;
import java.util.List;
import lang.*;

public class EvaluableCreator {
    public static Evaluable fromExpression(LispExpression le) {
        if (le instanceof Cons c) {
            return fromList(c);
        }
        else {
            return fromAtom(le);
        }
    }

    // crea i sotto-evaluable necessari e ci costruisce l'Evaluable composto
    public static Evaluable fromList(Cons c) {
        if(!c.isProperList())
            throw new InvalidParameterException
                ("cannot evaluate an improper cons list");
        if (c.getCar() instanceof LispSymbol sym) {
            // is it a special form?
            switch(sym.getName()) {
            case "if":
                return new IfEvaluable(fromExpression(c.nth(1)),
                                       fromExpression(c.nth(2)), 
                                       fromExpression(c.nth(3))); 
            case "while":
                return new WhileLoopEvaluable(fromExpression(c.nth(1)),
                                              sequenceFromConsList((Cons)c.nthCdr(2)));
            case "let":
                LispExpression frameDesc = c.nth(1);
                LispExpression body = c.nthCdr(2);
                if (frameDesc instanceof Cons desc && body instanceof Cons bod) {
                    List<LispSymbol> names = Utils.toJavaList(desc)
                        .stream()
                        .map(bind -> (LispSymbol)((Cons)bind).nth(0))
                        .toList();
                    List<Evaluable> vals = Utils.toJavaList(desc)
                        .stream()
                        .map(bind -> fromExpression(((Cons)bind).nth(1)))
                        .toList();
                    return new LetEvaluation(names, vals, sequenceFromConsList(bod));
                }
                else
                    throw new InvalidParameterException("let form is malformed");
            case "do":
            case "define":
            case "set":
                if(c.nth(1) instanceof LispSymbol varName)
                    return new SetEvaluable(varName, fromExpression(c.nth(2)));
            case "begin":
                return sequenceFromConsList(c);
            }

            // is it a builtin function, then?
            if (BuiltinFuncallEvaluable.isBuiltinFunctionName(sym))
                return new BuiltinFuncallEvaluable
                    (sym,
                     Utils.toJavaList((LispList)c.getCdr())
                     .stream()
                     .map(arg -> fromExpression(arg))
                     .toList());

            // in this case, it must be a plain funcall
            return null;
            
        }
        else {
            System.out.println("cazzo");
            return null;
        }
    }

    static boolean isSpecialFormName(LispSymbol sym) {
        String[] specialFormNames =
            {
            "if", "while", "do", "begin",
            "let", "set", "define"
            };
        for (String s : specialFormNames) {
            if(sym.getName().equals(s))
                return true;
        }
        return false;
    }

    static SequenceEvaluable sequenceFromConsList(LispList c) {
        List<Evaluable> evals = Utils.toJavaList(c)
            .stream()
            .map(exp -> fromExpression(exp))
            .toList();
        return new SequenceEvaluable(evals);
    }

    public static Evaluable fromAtom(LispExpression exp) {
        if (exp instanceof LispSymbol sym)
            return new LookupEvaluable(sym);
        else
            return new ConstantEvaluable(exp);
    }
}
