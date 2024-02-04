package org.jelly.eval.evaluable.formbuild;
import org.jelly.eval.evaluable.Evaluable;

public interface FormBuilder {
    void ensure(Object o) throws MalformedFormException;
    Evaluable buildEnsured(Object o);
}
