package eval;

import java.util.List;

public interface Procedure {
    // no env, does not necessarily form a closure
    public Object call(List<Object> arguments);
}
