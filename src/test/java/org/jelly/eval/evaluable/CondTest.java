package org.jelly.eval.evaluable;

import org.jelly.lang.data.Constants;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CondTest extends BaseEvaluableTest {
    @Test
    public void testCond() {
        assertEquals(Constants.UNDEFINED, eval("(cond (#f \"mamma\")" +
                "      (#f \"mia\")" +
                "      (#f \"pizzeria\"))"));

        assertEquals("pizzeria", eval("(cond (#f \"mamma\")" +
                "      (#f \"mia\")" +
                "      (#t \"pizzeria\"))"));

        assertEquals("mia", eval("(cond (#f \"mamma\")" +
                "      (#t \"mia\")" +
                "      (#t \"pizzeria\"))"));

        assertEquals("mamma", eval("(cond (#t \"mamma\")" +
                "      (#t \"mia\")" +
                "      (#t \"pizzeria\"))"));
    }
}
