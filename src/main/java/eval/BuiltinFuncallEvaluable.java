package eval;

import java.security.InvalidParameterException;
import java.util.List;

import java.util.Optional;
import lang.LispSymbol;
import lang.LispValue;
import lang.Constants;
import lang.LispExpression;
import lang.LispList;
import lang.Cons;
import lang.Ops;

public class BuiltinFuncallEvaluable implements Evaluable {
    private LispSymbol funSym;
    private List<Evaluable> uncomputed_args;

    public BuiltinFuncallEvaluable(LispSymbol funSym, List<Evaluable> args) {
        this.funSym = funSym;
        this.uncomputed_args = args;
    }

    @Override
    public LispExpression eval(Environment env) {
        return this.call(env);
    }

    private static String[] builtinFunNames = {"cons", "car", "cdr",
                                               "+", "-", "*", "/", ">", "<", "=", "<=", ">=", "!=",
                                               "print"};
    public static boolean isBuiltinFunctionName(LispSymbol sym) {
        for(String s : builtinFunNames) {
            if (sym.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    LispExpression call(Environment env) throws InvalidParameterException {
        String funName = funSym.getName();
        List<LispExpression> values = uncomputed_args.stream().map(exp -> exp.eval(env)).toList();
        switch (funName) {
        case "cons":
            if(values.size()!= 2)
                throw new InvalidParameterException
                    ("cons must be called with two argument");
            return new Cons(values.get(0), values.get(1));
        case "car":
            if(values.size()!= 1)
                throw new InvalidParameterException
                    ("car must be called with just one argument");
            if(values.get(0) instanceof LispList ll)
                return ll.getCar();
            throw new InvalidParameterException
                ("cannot call car on " + values.get(0) + "as it is not a list"); 
        case "cdr":
            if(values.size()!= 1)
                throw new InvalidParameterException
                    ("cdr must be called with just one argument");
            if(values.get(0) instanceof LispList ll)
                return ll.getCdr();
            throw new InvalidParameterException
                ("cannot call cdr on " + values.get(0) + "as it is not a list"); 
        case "+":
            if(!allNumeric(values))
                throw new InvalidParameterException
                    ("addition expects numeric arguments");
            if(allIntegers(values))
                return new LispValue<Integer>(integerSum(values));
            return new LispValue<Double>(doubleSum(values));
        case "-":
            if(!allNumeric(values))
                throw new InvalidParameterException
                    ("subtraction expects numeric arguments");
            if(allIntegers(values))
                return new LispValue<Integer>(integerDiff(values));
            return new LispValue<Double>(doubleDiff(values));
        case "*":
            if(!allNumeric(values))
                throw new InvalidParameterException
                    ("multiplication expects numeric arguments");
            if(allIntegers(values))
                return new LispValue<Integer>(integerProduct(values));
            return new LispValue<Double>(doubleProduct(values));
        case "/":
            if(values.size() != 2)
                throw new InvalidParameterException
                    ("division expects exactly 2 arguments");
            if(isZero(values.get(1)))
                throw new InvalidParameterException
                    ("cannot divide by 0");
            if(!allNumeric(values))
                throw new InvalidParameterException
                    ("division expects numeric arguments");
            return new LispValue<Double>
                (getDouble(values.get(0))/getDouble(values.get(0)));
        case ">":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                    ("> expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return new LispValue<Boolean> (getInteger(values.get(0)) >
                                               getInteger(values.get(1)));
            else
                return new LispValue<Boolean> (getDouble(values.get(0)) >
                                               getDouble(values.get(1)));
        case"<":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                    ("< expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return new LispValue<Boolean> (getInteger(values.get(0)) <
                                               getInteger(values.get(1)));
            else
                return new LispValue<Boolean> (getDouble(values.get(0)) <
                                               getDouble(values.get(1)));
        case "=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                    ("= expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return new LispValue<Boolean> (getInteger(values.get(0)) ==
                                               getInteger(values.get(1)));
            else
                return new LispValue<Boolean> (getDouble(values.get(0)) ==
                                               getDouble(values.get(1)));
        case "<=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                    ("<= expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return new LispValue<Boolean> (getInteger(values.get(0)) <=
                                               getInteger(values.get(1)));
            else
                return new LispValue<Boolean> (getDouble(values.get(0)) <=
                                               getDouble(values.get(1)));
        case ">=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                    (">= expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return new LispValue<Boolean> (getInteger(values.get(0)) >=
                                               getInteger(values.get(1)));
            else
                return new LispValue<Boolean> (getDouble(values.get(0)) >=
                                               getDouble(values.get(1)));
        case "!=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                    ("!= expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return new LispValue<Boolean> (getInteger(values.get(0)) !=
                                               getInteger(values.get(1)));
            else
                return new LispValue<Boolean> (getDouble(values.get(0)) !=
                                               getDouble(values.get(1)));

        case "print": // fatto principalmente per avere il sistema "subito" in uno stato dove può dare feedback
            int i = 0;
            for (Object o : values) {
                System.out.println(i + " : " + o);
            }
            return Constants.NIL;
        }
        return null;
    }

    double getDouble(LispExpression le) throws InvalidParameterException {
        if(le instanceof LispValue lv) {
            if(lv.get() instanceof Double d)
                return d;
            if(lv.get() instanceof Integer i)
                return (double)i;
        }
        throw new InvalidParameterException
            ("cannot get a double from " + le.toString() + " not a lisp number");
    }

    int getInteger (LispExpression le) throws InvalidParameterException {
        if(le instanceof LispValue lv) {
            if(lv.get() instanceof Integer i)
                return i;
        }

        throw new InvalidParameterException
            ("cannot get a integer from " + le.toString() + " not a lisp integer");
    }

    boolean isZero(LispExpression lv) throws InvalidParameterException {
        return getDouble(lv) == 0;
    }

    boolean allNumeric(List<LispExpression> exprs) {
        return exprs.stream().allMatch(a->Ops.isNumber(a));
    }

    boolean allIntegers(List<LispExpression> exprs) {
        return exprs.stream().allMatch(a->Ops.isInteger(a));
    }

    int integerSum(List<LispExpression> exprs) {
        Optional<LispExpression> le = exprs.stream().reduce
            ((LispExpression a, LispExpression b) ->
             new LispValue<Integer>(getInteger(a) + getInteger(b)));

        if(le.isPresent()) {
            return getInteger(le.get());
        }
        return 0;
    }

    double doubleSum(List<LispExpression> exprs) {
        Optional<LispExpression> le = exprs.stream().reduce
            ((LispExpression a, LispExpression b) ->
             new LispValue<Double>(getDouble(a) + getDouble(b)));

        if(le.isPresent()) {
            return getDouble(le.get());
        }
        return 0;
    }

    int integerDiff(List<LispExpression> exprs) {
        if(exprs.size() == 0) return 0;
        return getInteger(exprs.get(0)) - integerSum(exprs.subList(1,exprs.size()));
    }

    double doubleDiff(List<LispExpression> exprs) {
        if(exprs.size() == 0) return 0;
        return getDouble(exprs.get(0)) - doubleSum(exprs.subList(1,exprs.size()));
    }


    int integerProduct(List<LispExpression> exprs) {
        Optional<LispExpression> le = exprs.stream().reduce
            ((LispExpression a, LispExpression b) ->
             new LispValue<Integer>(getInteger(a) * getInteger(b)));

        if(le.isPresent()) {
            return getInteger(le.get());
        }
        return 0;
    }

    double doubleProduct(List<LispExpression> exprs) {
        Optional<LispExpression> le = exprs.stream().reduce
            ((LispExpression a, LispExpression b) ->
             new LispValue<Double>(getDouble(a) * getDouble(b)));

        if(le.isPresent()) {
            return getDouble(le.get());
        }
        return 0;
    }
}

    
