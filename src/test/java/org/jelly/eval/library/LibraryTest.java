package org.jelly.eval.library;

import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.lang.data.Cons;
import org.jelly.lang.data.Constants;
import org.jelly.lang.data.Symbol;
import org.jelly.utils.ConsUtils;
import org.jelly.utils.ListBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    // test in negativo (rende gli errori giusti)
}
