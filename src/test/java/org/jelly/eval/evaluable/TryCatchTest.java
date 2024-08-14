package org.jelly.eval.evaluable;

import org.jelly.eval.evaluable.errors.CatchFailedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TryCatchTest extends BaseEvaluableTest {
    @Test
    public void testDoesNotBlowUpImmediately() {
        eval("(try 10)");
    }

    @Test
    public void testNoThrowReturn() {
        assertEquals(10, eval("(try 10)"));
    }

    @Test
    public void testCatchReturn() {
        assertEquals("oh yes", eval("(try (error \"oh no\") (catch (JellyError je) \"oh yes\"))"));
    }

    @Test
    public void testSkipCatchReturn() {
        assertEquals("oh yes", eval("(try (error \"oh no\")" +
                "     (catch (FileNotFoundException fnf)" +
                "       \"oh maybe\")" +
                "     (catch (JellyError je)" +
                "       \"oh yes\"))"));
    }

    @Test
    public void testSkipAllCatchThrows() {
        assertThrows(CatchFailedException.class,
                () ->eval("(try (error \"oh no\")" +
                        "     (catch (FileNotFoundException fnf)" +
                        "       \"oh maybe\")" +
                        "     (catch (NullPointerException nl)" +
                        "       \"oh yes\"))"));
    }
}
