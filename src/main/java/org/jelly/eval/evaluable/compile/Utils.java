package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.compile.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.AstHandling;
import org.jelly.utils.ConsUtils;

import java.util.*;
import java.util.function.Predicate;

public class Utils {
    private static String renderFormPrototype(String formName, String[] childrenNames) {
        StringBuilder formPrototypeBuilder = new StringBuilder();
        formPrototypeBuilder.append("(").append(formName);
        Arrays.stream(childrenNames).forEach(a -> formPrototypeBuilder.append(" <").append(a).append(">"));
        return formPrototypeBuilder.append(")").toString();
    }

    static void checkFlatFixed(Cons form, String formName, String[] childrenNames) throws MalformedFormException {
        /*
         * encapsulates some repeated behaviour in validating certain "flat" forms
         *
         * this function works under the following assumptions
         * 1. the form has a fixed amount of children as dictated by the syntax
         * 2. the syntax only dictates the validity of the immediate children of the form, that is
         * it only cares about the fact that every element of the list starting with if, when, unless, &Co. are correct
         * this does not work for things like let, do, or lambda, as those demand the parameters to fit in a certain
         * nested structure, not in a plain "all children are good" structure
         *
         * these assumptions are common enough that it made sense to write this function
         */
        if(! startsWithSym(form, formName)) {
            throw new RuntimeException("expected form to start with " + formName + " symbol, but it starts with " + form.getCar());
        }
        if(form.length() != (childrenNames.length + 1)) {
            String formPrototype = renderFormPrototype(formName, childrenNames);
            throw new MalformedFormException("form must have exactly " + childrenNames.length + " children in the form " + formPrototype);
        }
        for(int i = 1; i<form.length(); ++i) {
            try {
                Compiler.checkExpression(form.nth(i));
            } catch(MalformedFormException mfe) {
                throw new MalformedFormException(formName + " form is malformed because " + childrenNames[i-1] + " is malformed");
            }
        }
    }

    static boolean startsWithSym(ConsList ll, String s) {
        return ((Symbol)ll.getCar()).name().equals(s);
    }

    static void checkSequenceList(ConsList ll) throws MalformedFormException {
        List<Object> l = ListUtils.toJavaList(ll);
        for (Object o : l) {
            Compiler.checkExpression(o);
        }
    }

    static SequenceEvaluable sequenceFromConsList(ConsList c) {
        return new SequenceEvaluable(toEvaluableList(c));
    }

    static SequenceEvaluable sequenceFromJavaList(List<Object> lst) {
        return new SequenceEvaluable(toEvaluableList(lst));
    }

    static List<Evaluable> toEvaluableList(ConsList c) {
        return ListUtils.toStream(c)
            .map(Compiler::compileExpression)
            .toList();
    }

    static List<Evaluable> toEvaluableList(List<Object> lst) {
        return lst.stream()
                .map(Compiler::compileExpression)
                .toList();
    }

    static void ensureLambdaListAST(ConsList ll) throws MalformedFormException {
        List<Object> l = ListUtils.toJavaList(ll);
        for(Object o : l) {
            if(!(o instanceof Symbol)) {
                throw new MalformedFormException(o + " should not be in a lambda list, only symbols can be there");
            }
        }
    }

    static Map<Symbol, Symbol> alistToMap(Cons c) {
        Map<Symbol, Symbol> res = new HashMap<>();
        ConsUtils.toStream(c).forEach(a -> res.put(AstHandling.requireSymbol(ConsUtils.nth(a, 0)),
                                                           AstHandling.requireSymbol(ConsUtils.nth(a, 1))));
        return res;
    }

    static void checkSymbolAlist(Cons symbolAList)  throws MalformedFormException {
        /*
         * checks for a structure like ((a b) (c d) ...)
         */
        for(Object elt : ConsUtils.toList(symbolAList)) {
            checkListOfSize(ConsUtils.requireCons(elt), 2);
            checkListType(ConsUtils.requireCons(elt), Symbol.class);
        }
    }

    static void checkSymbolList(Cons symbolList) throws MalformedFormException {
        checkListType(symbolList, Symbol.class);
    }

    private static void checkListType(Cons list, Class<?> cls) throws MalformedFormException {
        Optional<Object> culprit = ConsUtils.toStream(list).filter(a -> !cls.isInstance(a)).findFirst();
        if(culprit.isPresent())
            throw new MalformedFormException("list was supposed to be only composed of "
                    + cls.getCanonicalName()
                    + " objects, but it cocntains "
                    + culprit.get()
                    + ", of type " + culprit.get().getClass().getCanonicalName());
    }

    static void checkListOfSize(Cons list, int size)  throws MalformedFormException {
        if(!list.isProperList())
            throw new MalformedFormException("list was supposed to be an exact list of length " + size + " but it's not a proper list");
        if(list.length() != size)
            throw new MalformedFormException("list was supposed to be an exact list of length " + size + " but it is of length " + list.length());
    }

    static void nothing() { }

    static void ensureOnlyTailSatisfies(Cons list, Predicate<Object> pred) throws MalformedFormException {
        Optional<Object> culprit = ConsUtils.toStream(list).dropWhile(pred.negate()).dropWhile(pred).findFirst();
        if(culprit.isPresent()) {
            throw new MalformedFormException("forms satisfying predicate exist outside of tail, form"
                    + culprit.get()
                    + " not satisfying predicate exists in tail");
        }
    }

    static void ensureOnlyHeadSatisfies(Cons list, Predicate<Object> pred) throws MalformedFormException {
        Optional<Object> culprit = ConsUtils.toStream(list).dropWhile(pred).dropWhile(pred.negate()).findFirst();
        if(culprit.isPresent()) {
            throw new MalformedFormException("forms satisfying predicate exist outside of head, form"
                    + culprit.get()
                    + " not satisfying predicate exists in head");
        }
    }
}
