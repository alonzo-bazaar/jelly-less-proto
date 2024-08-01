package org.jelly.eval.library;

import org.jelly.eval.environment.errors.EnvironmentException;
import org.jelly.eval.environment.errors.UnboundVariableException;
import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.ConsUtils;
import org.jelly.utils.ListBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest extends BaseEvaluableTest {
    // test in positivo (runna)
    @Test
    public void testBeginDoesNotBlowUpImmediately() {
        eval("(define-library (test inizio)" +
                "  (begin" +
                "    (define x 10)))");
    }

    @Test
    public void testExportDoesNotBlowUpImmediately() {
        eval("(define-library (test esporta)" +
                "  (export something)" +
                "  (begin" +
                "    (define something 20)))");
    }

    @Test
    public void testImportDoesNotBlowUpImmediately() {
        eval("(define-library (test esporta)" +
                "  (export something)" +
                "  (begin" +
                "    (define something 20)))");

        eval("(define-library (test importa)" +
                "  (import (test esporta)))");

        eval("(define-library (test importa solo)" +
                "  (import (only (test esporta) something)))");

        eval("(define-library (test importa eccetto)" +
                "  (import (except (test esporta) something)))");
    }

    // test in positivo (valor corretti)
    @Test
    public void testImportCorrectValue() {
        eval("(define-library (test esporta)" +
                "  (export something square)" +
                "  (begin" +
                "    (define something 20)" +
                "    (define (square x) (* x x))))");

        eval("(import (test esporta))");
        assertEquals(20, (int)eval("something"));
        assertEquals(400, (int)eval("(square something)"));
    }

    @Test
    public void testInvisibleOutsideLibrary() {
        eval("(define-library (test esporta)" +
                "  (export something)" +
                "  (begin" +
                "    (define something 20)))");

        assertThrows(UnboundVariableException.class, () -> lookup("something"));
    }

    @Test
    public void testUnchangedOutsideLibrary() {
        eval("(define something 10)");
        eval("(define-library (test esporta)" +
                "  (export something)" +
                "  (begin" +
                "    (define something 20)))");

        assertEquals(10, (int)eval("something"));
    }

    // test in negativo (rende gli errori giusti)
}
