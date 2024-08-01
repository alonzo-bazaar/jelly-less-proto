package org.jelly.eval.procedure;

import org.jelly.eval.environment.errors.UnboundVariableException;
import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.parse.errors.ParsingException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClosureTest extends BaseEvaluableTest {
    /* esempio da sicp (pagina 298 della versione texinfo, pagina 326 del pdf
     * - Modularity, Objects, and State
     * -- Assignment and Local State
     * --- Local State Variables
     */

    @Test
    public void testSicpWithdrawGlobal() throws ParsingException {
        eval("(define balance 100)");
        
        eval("(define (withdraw amount)" +
                   "      (if (>= balance amount)" +
                   "          (begin (set! balance (- balance amount))" +
                   "                 balance)" +
                   "          \"Insufficient funds\"))");

        assertEquals((int)eval("(withdraw 25)"), 75);
        assertEquals((int)eval("(withdraw 25)"), 50);
        assertEquals((String)eval("(withdraw 60)"), "Insufficient funds");
        assertEquals((int)eval("(withdraw 15)"), 35);
    }

    @Test
    public void testSicpWithdrawGlobalLambda() throws ParsingException {
        eval("(define balance 100)");
        
        eval("(define withdraw (lambda (amount)" +
                   "      (if (>= balance amount)" +
                   "          (begin (set! balance (- balance amount))" +
                   "                 balance)" +
                   "          \"Insufficient funds\")))");

        assertEquals(75, (int)eval("(withdraw 25)"));
        assertEquals(50, (int)eval("(withdraw 25)"));
        assertEquals("Insufficient funds", (String)eval("(withdraw 60)"));
        assertEquals(35, (int)eval("(withdraw 15)"));
    }


    /* esempio da sicp (pagina 300 della versione texinfo, pagina 328 del pdf
     */
    @Test
    public void testSicpWithdrawLocal() throws ParsingException {
        eval("(define new-withdraw " +
                   "  (let ((balance 100)) " +
                   "    (lambda (amount) " +
                   "      (if (>= balance amount) " +
                   "          (begin (set! balance (- balance amount)) " +
                   "                 balance) " +
                   "          \"Insufficient funds\"))))");

        assertEquals(75, (int)eval("(new-withdraw 25)"));
        assertEquals(50, (int)eval("(new-withdraw 25)"));
        assertEquals("Insufficient funds", (String)eval("(new-withdraw 60)"));
        assertEquals(35, (int)eval("(new-withdraw 15)"));

        assertThrows(UnboundVariableException.class, () -> lookup("balance"));
    }

    /* pagina 301, (329 del pdf)
     */
    @Test
    public void testSicpMakeWithdraw() throws ParsingException {
        eval("(define (make-withdraw balance) " +
                   " (lambda (amount) " +
                   "  (if (>= balance amount) " +
                   "      (begin (set! balance (- balance amount)) " +
                   "       balance) " +
                   "       \"Insufficient funds\"))) ");
        
        eval("(define W1 (make-withdraw 100))");
        eval("(define W2 (make-withdraw 100))");

        assertEquals(50, (int)eval("(W1 50)"));
        assertEquals(30, (int)eval("(W2 70)"));
        assertEquals("Insufficient funds", (String)eval("(W2 40)"));
        assertEquals(10, (int)eval("(W1 40)"));
    }

    /* pagina 301, (329 del pdf)
     */
    @Test
    public void testSicpMakeWithdrawLambda() throws ParsingException {
        eval("(define make-withdraw " +
                   "(lambda (balance) " +
                   " (lambda (amount) " +
                   "  (if (>= balance amount) " +
                   "      (begin (set! balance (- balance amount)) " +
                   "       balance) " +
                   "       \"Insufficient funds\")))) ");
        
        eval("(define W1 (make-withdraw 100))");
        eval("(define W2 (make-withdraw 100))");

        assertEquals(50, (int)eval("(W1 50)"));
        assertEquals(30, (int)eval("(W2 70)"));
        assertEquals("Insufficient funds", (String)eval("(W2 40)"));
        assertEquals(10, (int)eval("(W1 40)"));
    }

}
