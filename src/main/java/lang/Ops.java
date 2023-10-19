package lang;

import java.security.InvalidParameterException;

public final class Ops {
    /**
     * un unico functoide per dominare tutti gli altri
     * la si pensi come una versione cresciuta male di Math
     * ma le costanti sono in una classe a parte
     */

    /* la lista in lisp è una "collaborazione" tra i tipi cons e
     * nil, dato ciò mettere queste operazioni in una delle due classi
     * Cons o NilValue porterebbe in entrambi i casi a codice
     * un pochino imbrattato
     */
    
    // (<roba> instanceof <tipo> <altraroba>) è descritto in
    // https://docs.oracle.com/en/java/javase/17/language/pattern-matching-instanceof-operator.html#GUID-843060B5-240C-4F47-A7B0-95C42E5B08A7
    // lo switch con i tipi (se lo metto) è descritto in
    // https://docs.oracle.com/en/java/javase/17/language/pattern-matching-switch-expressions-and-statements.html#GUID-E69EEA63-E204-41B4-AA7F-D58B26A3B232
    public static int length(LispExpression le)
        throws InvalidParameterException {
        if(isNil(le))
            return 0;
        else if(le instanceof Cons c)
            return c.length();
        throw new InvalidParameterException
            ("cannot measure lenght of non list variable " + le.toString()
             + "of type" + le.getClass().getCanonicalName());
    }

    public static LispExpression car(LispExpression le)
        throws InvalidParameterException {
        if(isNil(le))
            return Constants.NIL;
        if(le instanceof Cons c)
            return c.getCar();
        throw new InvalidParameterException
            ("cannot extract car of non list variable " + le.toString()
             + "of type" + le.getClass().getCanonicalName());
    }

    public static LispExpression cdr(LispExpression le)
        throws InvalidParameterException {
        if(isNil(le))
            return Constants.NIL;
        if(le instanceof Cons c)
            return c.getCdr();
        throw new InvalidParameterException
            ("cannot extract cdr of non list variable " + le.toString()
             + "of type" + le.getClass().getCanonicalName());
    }

    public static boolean isInteger(LispExpression le) {
        if(le instanceof LispValue lv) {
            return lv.get() instanceof Integer;
        }
        return false;
    }

    public static boolean isReal(LispExpression le) {
        if(le instanceof LispValue lv) {
            return lv.get() instanceof Double;
        }
        return false;
    }

    public static boolean isString(LispExpression le) {
        if(le instanceof LispValue lv) {
            return lv.get() instanceof String;
        }
        return false;
    }

    public static boolean isCharacter(LispExpression le) {
        if(le instanceof LispValue lv) {
            return lv.get() instanceof Character;
        }
        return false;
    }

    public static boolean isNumber(LispExpression le) {
        return isInteger(le) || isReal(le);
    }

    public static boolean isSymbol(LispExpression le) {
        return le instanceof LispSymbol;
    }

    public static boolean isCons(LispExpression le) {
        return le instanceof Cons;
    }

    public static boolean isNil(LispExpression le) {
        // un unico nil
        return le == Constants.NIL;
    }
}
