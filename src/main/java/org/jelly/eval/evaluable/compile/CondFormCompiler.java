package org.jelly.eval.evaluable.compile;

import org.jelly.eval.evaluable.ConstantEvaluable;
import org.jelly.eval.evaluable.Evaluable;
import org.jelly.eval.evaluable.IfEvaluable;
import org.jelly.eval.evaluable.errors.MalformedFormException;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.ConsList;
import org.jelly.lang.data.Constants;
import org.jelly.utils.ConsUtils;

import java.util.List;
import java.util.Optional;

public class CondFormCompiler implements FormCompiler {
    Cons c;

    public CondFormCompiler(Cons c) {
        this.c = c;
    }

    @Override
    public IfEvaluable compile() {
        List<Object> checks = ConsUtils.toStream(ConsUtils.requireCons(c.getCdr()))
                .map(ConsUtils::requireCons)
                .map(Cons::getCar)
                .toList();

        List<Cons> bodies = ConsUtils.toStream(ConsUtils.requireCons(c.getCdr()))
                .map(ConsUtils::requireCons)
                .map(Cons::getCdr)
                .map(ConsUtils::requireCons)
                .toList();

        return fromClauses(checks, bodies);
    }

    private static IfEvaluable fromClauses(List<Object> checks, List<Cons> bodies) {
        if(checks.isEmpty()) return new IfEvaluable(new ConstantEvaluable(Constants.TRUE),
                                                    new ConstantEvaluable(Constants.UNDEFINED),
                                                    new ConstantEvaluable(Constants.UNDEFINED));
        return new IfEvaluable(Compiler.compileExpression(checks.getFirst()),
                               Utils.sequenceFromConsList(bodies.getFirst()),
                               fromClauses(checks.subList(1, checks.size()), bodies.subList(1, bodies.size())));
    }

    @Override
    public void check() throws MalformedFormException {
        try {
            Optional<Cons> culprit = ConsUtils.toStream(ConsUtils.requireCons(c.getCdr()))
                    .map(ConsUtils::requireCons)
                    .filter(c -> c.length() < 2)
                    .findFirst();
            if(culprit.isPresent()) {
                throw new MalformedFormException("cons expects all clauses to have at least length 2, clause "
                        + culprit.get() + " has invalid length "
                        + culprit.get().length());
            }

            try {
                List<Object> clauses =  ConsUtils.toStream(ConsUtils.requireCons(c.getCdr()))
                        .map(ConsUtils::requireCons)
                        .map(ConsList::getCar)
                        .toList();
                for(Object o : clauses)
                    Compiler.checkExpression(o);
            } catch(MalformedFormException mfe) {
                throw new MalformedFormException("cons expression is malformed because clause condition is malformed", mfe);
            }

            try {
                List<Cons> clauses =  ConsUtils.toStream(ConsUtils.requireCons(c.getCdr()))
                        .map(ConsUtils::requireCons)
                        .map(ConsList::getCdr)
                        .map(ConsUtils::requireCons)
                        .toList();
                for(Cons c : clauses)
                    Utils.checkSequenceList(c);
            } catch(MalformedFormException mfe) {
                throw new MalformedFormException("cons expression is malformed because clause body is malformed", mfe);
            }
        }
        catch(ClassCastException cce) {
            throw new MalformedFormException("cannot compile cons form because ast element is of incorrect type", cce);
        } catch(Throwable t) {
            throw new MalformedFormException("cannot compile cons form, generic error occurred", t);
        }
    }
}
