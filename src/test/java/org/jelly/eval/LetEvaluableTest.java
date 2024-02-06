package org.jelly.eval;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LetEvaluableTest extends BaseEvaluableTest {
    @Test
    public void testNormalLoopOneVar() {
        assertEquals((int)fromString( "  (let ((a 10)) a)").eval(env), 10);
    }
}
