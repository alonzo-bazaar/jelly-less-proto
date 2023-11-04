package eval;

import java.lang.Runnable;
import java.util.function.Function;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.Optional;
import lang.LispSymbol;
import lang.Constants;
import lang.LispList;
import lang.Cons;

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

    private final static String[] builtinFunNames = {
            "cons", "car", "cdr",
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
        List<Object> values = uncomputed_args.stream().map(exp -> exp.eval(env)).toList();
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
                return integerSum(values);
            return doubleSum(values);
        case "-":
            if(!allNumeric(values))
                throw new InvalidParameterException
                    ("subtraction expects numeric arguments");
            if(allIntegers(values))
                return integerDiff(values);
            return doubleDiff(values);
        case "*":
            if(!allNumeric(values))
                throw new InvalidParameterException
                    ("multiplication expects numeric arguments");
            if(allIntegers(values))
                return integerProduct(values);
            return doubleProduct(values);
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
            return (getDouble(values.get(0))/getDouble(values.get(1)));
        case ">":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                    ("> expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return (Integer)values.get(0) > (Integer)values.get(1);
            else
                return getDouble(values.get(0)) > getDouble(values.get(1));
        case"<":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                        ("> expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return (Integer)(values.get(0)) < (Integer)(values.get(1));
            else
                return getDouble(values.get(0)) < getDouble(values.get(1));
        case "=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                        ("> expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return (int)(values.get(0)) == (int)(values.get(1));
            else
                return getDouble(values.get(0)) == getDouble(values.get(1));
        case "<=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                        ("> expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return (Integer)(values.get(0)) <= (Integer)(values.get(1));
            else
                return getDouble(values.get(0)) <= getDouble(values.get(1));
        case ">=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                        ("> expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return (Integer)(values.get(0)) >= (Integer)(values.get(1));
            else
                return getDouble(values.get(0)) >= getDouble(values.get(1));
        case "!=":
            if(values.size() != 2 || !allNumeric(values))
                throw new InvalidParameterException
                        ("> expects exactly 2 numeric arguments");
            if(allIntegers(values))
                return (int)(values.get(0)) != (int)(values.get(1));
            else
                return getDouble(values.get(0)) != getDouble(values.get(1));

        case "print": // fatto principalmente per avere il sistema "subito" in uno stato dove può dare feedback
            int i = 0;
            for (Object o : values) {
                System.out.println(i + " : " + o);
                i++;
            }
            return Constants.NIL;
        }
        return null;
    }

    boolean isZero(Object lv) throws InvalidParameterException {
        return getDouble(lv) == 0;
    }

    boolean allNumeric(List<Object> exprs) {
        return exprs.stream().allMatch(a-> a instanceof Integer || a instanceof Double);
    }

    boolean allIntegers(List<Object> exprs) {
        return exprs.stream().allMatch(a->a instanceof Integer);
    }

    double getDouble(Object o) {
        return switch (o) {
            case Integer i -> (double)((int)i);
            case Double d -> (double)d;
            default -> 0;
        };
    }

    int getInt(Object o) {
        return switch (o) {
            case Integer i -> (int)i;
            case Double d -> (int)((double)d);
            default -> 0;
        };
    }

    Integer integerSum(List<Object> exprs) {
        if(exprs.isEmpty()) return 0;
        Optional<Object> sum = exprs.stream().reduce ((Object a, Object b) -> (Integer)a + (Integer)b);
        return sum.map(this::getInt).orElse(0);
    }

    Double doubleSum(List<Object> exprs) {
        if(exprs.isEmpty()) return 0.0;
        Optional<Object> sum = exprs.stream().reduce ((Object a, Object b) -> getDouble(a) + getDouble(b));
        return sum.map(this::getDouble).orElse(0.0);
    }


    Integer integerDiff(List<Object> exprs) {
        if(exprs.isEmpty()) return 0;
        return (Integer)(exprs.get(0)) - integerSum(exprs.subList(1,exprs.size()));
    }

    Double doubleDiff(List<Object> exprs) {
        if(exprs.isEmpty()) return 0.0;
        return getDouble(exprs.get(0)) - doubleSum(exprs.subList(1,exprs.size()));
    }


    Integer integerProduct(List<Object> exprs) {
        if(exprs.isEmpty()) return 1;
        Optional<Object> prod = exprs.stream().reduce((Object a, Object b) -> (Integer)(a) * (Integer)(b));
        return prod.map(this::getInt).orElse(1);
    }

    double doubleProduct(List<Object> exprs) {
        if(exprs.isEmpty()) return 1.0;
        Optional<Object> prod = exprs.stream().reduce((Object a, Object b) -> getDouble(a) * getDouble(b));
        return prod.map(this::getDouble).orElse(1.0);
    }
}
