package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.SequenceEvaluable;
import org.jelly.eval.evaluable.compile.Compiler;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.utils.ListUtils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispSymbol;

import java.util.Arrays;
import java.util.List;

public class Utils {
    private static String renderFormPrototype(String formName, String[] childrenNames) {
        StringBuilder formPrototypeBuilder = new StringBuilder();
        formPrototypeBuilder.append("(").append(formName);
        Arrays.stream(childrenNames).forEach(a -> formPrototypeBuilder.append(" <").append(a).append(">"));
        return formPrototypeBuilder.append(")").toString();
    }

    public static void checkFlatFixed(Cons form, String formName, String[] childrenNames) throws MalformedFormException {
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

    public static boolean startsWithSym(LispList ll, String s) {
        return ((LispSymbol)ll.getCar()).getName().equals(s);
    }

    public static void checkSequenceList(LispList ll) throws MalformedFormException {
        List<Object> l = ListUtils.toJavaList(ll);
        for (Object o : l) {
            Compiler.checkExpression(o);
        }
    }

    public static SequenceEvaluable sequenceFromConsList(LispList c) {
        return new SequenceEvaluable(toEvaluableList(c));
    }

    public static List<Evaluable> toEvaluableList(LispList c) {
        return ListUtils.toStream(c)
            .map(Compiler::compileExpression)
            .toList();
    }
}
