package org.jelly.eval.evaluable;

import java.util.List;

import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.eval.runtime.EnvFrame;
import org.jelly.eval.runtime.Environment;
import org.jelly.eval.runtime.errors.VariableDoesNotExistException;
import org.jelly.eval.utils.Utils;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.LispList;
import org.jelly.lang.data.LispLists;
import org.jelly.lang.data.LispSymbol;
import org.jetbrains.annotations.NotNull;

public class DoForm implements Form {
    private List<LispSymbol> vars;
    private List<Evaluable> initForms;
    private List<Evaluable> updateForms;
    private SequenceEvaluable body;
    private Evaluable stopCondition;
    private Evaluable returnOnStop;

    public DoForm(List<LispSymbol> vars,
                  List<Evaluable> initForms,
                  List<Evaluable> updateForms,
                  SequenceEvaluable body,
                  Evaluable stopCondition,
                  Evaluable returnOnStop) {
        this.vars = vars;
        this.initForms = initForms;
        this.updateForms = updateForms;
        this.stopCondition = stopCondition;
        this.returnOnStop = returnOnStop;
        this.body = body;
    }

    @Override
    public Object eval(Environment env) {
        EnvFrame frame = new EnvFrame(vars, this.initForms
                                      .stream()
                                      .map(a -> a.eval(env))
                                      .toList());
        Environment exEnv = env.extend(frame);

        while (Utils.isFalse(stopCondition.eval(exEnv))) {
            body.eval(exEnv);
            updateFrame(frame, exEnv);
        }

        return returnOnStop.eval(exEnv);
    }

    private void updateFrame(EnvFrame frame, Environment env) {
        // il do normale aggiorna le variabili "in parallelo"
        // quindi senza che il valore aggiornato di una variabile
        // vada a influenzare il valore aggiornato di un'altra variable
        // quindi prima si aggiornano tutti, poi si assegnano di botta

        try {
            List<Object> updated = updateForms.stream().map(a -> a.eval(env)).toList();
            for (int i = 0; i < vars.size(); ++i) {
                env.set(vars.get(i), updated.get(i));
            }
        } catch(VariableDoesNotExistException ignored) {} // dovrebbe essere unreachable
    }

    @NotNull
    public static DoForm fromCheckedAST(Cons c) {
        Cons doVars = LispLists.requireCons(c.nth(1));
        Cons doStop = LispLists.requireCons(c.nth(2));
        LispList doBody = LispLists.requireList(c.nthCdr(3));

        List<LispSymbol> names = Utils.toStream(doVars)
                .map(var -> (LispSymbol)(((Cons)var).nth(0)))
                .toList();

        List<Evaluable> initForms = Utils.toStream(doVars)
                .map(var -> EvaluableCreator.fromExpression(((Cons)var).nth(1)))
                .toList();

        List<Evaluable> updateForms = Utils.toStream(doVars)
                .map(var -> EvaluableCreator.fromExpression(((Cons)var).nth(2)))
                .toList();

        Evaluable stopCondition = EvaluableCreator.fromExpression(doStop.nth(0));
        Evaluable returnOnStop = EvaluableCreator.fromExpression(doStop.nth(1));

        SequenceEvaluable body = FormBuilding.sequenceFromConsList(doBody);

        return new DoForm(names, initForms, updateForms,
                body,
                stopCondition, returnOnStop);
    }

    static void checkAST(Cons c) throws MalformedFormException {
        if(!FormBuilding.startsWithSym(c, "do"))
            throw new RuntimeException("do do do");
        if(c.length() < 3)
            throw new MalformedFormException("do form expects at least two arguments (do (<do-variable>*) <do-stop> <sequence-element>*)");

        try {
            checkVariablesAST(LispLists.requireList(c.nth(1))); // vars
            checkStopAST(LispLists.requireList(c.nth(2))); // stop
            FormBuilding.ensureSequenceList(LispLists.requireList(c.nthCdr(3))); // body
        } catch(ClassCastException cce) {
            throw new MalformedFormException("do form is malformed, most likely some subform has incorrect nesting, check the parentheses, the error in most likely somewhere in the variable declarations or in the stop condition\n", cce);
        } catch(MalformedFormException mfe) {
            throw new MalformedFormException("do form is malformed because child is malformed", mfe);
        }
    }

    private static void checkVariablesAST(LispList ll) throws MalformedFormException {
        List<Object> allVars = Utils.toJavaList(ll);
        for(Object o : allVars) {
            List<Object> var = Utils.toJavaList(LispLists.requireCons(o));
            if(var.size() != 3)
                throw new MalformedFormException("do variable must have exactly 3 elements, (<name> <init-form> <update-form>)");
            if(!(var.getFirst() instanceof LispSymbol))
                throw new MalformedFormException("do variable binding must starts valid variable name (with a symbol), " + var.get(0) + " is not a valid variable name");
            EvaluableCreator.ensureForm(var.get(1));
            EvaluableCreator.ensureForm(var.get(2));
        }
    }

    private static void checkStopAST(LispList ll) throws MalformedFormException {
        if(ll.length() > 2) {
            throw new MalformedFormException("do stop must have at most elements ([<stop-condition> [<stop-return>]])");
        }
    }
}
