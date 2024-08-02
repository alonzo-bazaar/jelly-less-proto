package org.jelly.eval.library;

import org.jelly.eval.environment.errors.UnboundVariableException;
import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.parse.errors.SyntaxTreeParsingException;
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
    // syntax shit
    // multiple import, rename export, prefix import
    @Test
    public void testMultipleImport() {
        eval("(define-library (first exporter)" +
                "  (export square)" +
                "  (begin" +
                "    (define (square x) (* x x))))");

        eval("(define-library (second exporter)" +
                "  (export cube)" +
                "  (begin" +
                "    (define (cube x) (* x x x))))");

        eval("(define-library (importer man)" +
                "  (import (only (first exporter) square)" +
                "           (second exporter))" +
                "  (export hexa)" +
                "  (begin (define (hexa x) (square (cube x)))))");


        eval("(import (importer man))");
        assertEquals(1_000_000, (int)eval("(hexa 10)"));
    }

    @Test
    public void testRenameExport() {
        eval("(define-library (renamed exporter)" +
                "  (export (rename prima dopo))" +
                "  (begin" +
                "    (define prima 10)))");

        eval("(import (renamed exporter))");
        assertEquals(10, (int)eval("dopo"));
    }

    @Test
    public void testPrefixImport() {
        eval("(define-library (arit)" +
                "  (export square cube)" +
                "  (begin" +
                "    (define (square x) (* x x))" +
                "    (define (cube x) (* x x x))))" +

                "(import (prefix (arit) arit-))");

        assertEquals(100, (int)eval("(arit-square 10)"));
        assertEquals(1000, (int)eval("(arit-cube 10)"));
    }

    // test in negativo (rende gli errori giusti)

    @Test
    public void testBlowsUpOnWrongImport() {
        // cannot import symbol
        assertThrows(SyntaxTreeParsingException.class, () ->
                eval("(define-library (test esporta)" +
                        "  (import something)" +
                        "  (begin" +
                        "    (define something 20)))"));

        // cannot import unexisting library
        assertThrows(NoSuchLibraryException.class, () ->
                eval("(define-library (test esporta)" +
                        "  (import (waluigi something))" +
                        "  (begin" +
                        "    (define something 20)))"));
    }

    @Test
    public void testBlowsUpOnWrongExport() {
        assertThrows(SyntaxTreeParsingException.class, () ->
                eval("(define-library (test esporta)" +
                        "  (export (something in the way))" +
                        "  (begin" +
                        "    (define something 20)))"));

        assertThrows(SyntaxTreeParsingException.class, () ->
                eval("(define-library (test esporta)" +
                        "  (export (replace something (in the way)))" +
                        "  (begin" +
                        "    (define something 20)))"));

        assertThrows(SyntaxTreeParsingException.class, () ->
                eval("(define-library (test esporta)" +
                        "  (export (replace something 20))" +
                        "  (begin" +
                        "    (define something 20)))"));
    }

    @Test
    public void testBlowsUpOnWrongBegin() {
        assertThrows(SyntaxTreeParsingException.class, () ->
                eval("(define-library (test esporta)" +
                        "  (export (replace something 20))" +
                        "  (begin" +
                        "    (set!)))"));
    }
}
