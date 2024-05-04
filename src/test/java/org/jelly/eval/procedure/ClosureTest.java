package org.jelly.eval.procedure;

import org.jelly.eval.evaluable.BaseEvaluableTest;
import org.jelly.parse.errors.ParsingException;

import org.junit.jupiter.api.Test;

import org.jelly.lang.data.LispSymbol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClosureTest extends BaseEvaluableTest {
    /* esempio da sicp (pagina 298 della versione texinfo, pagina 326 del pdf
     * - Modularity, Objects, and State
     * -- Assignment and Local State
     * --- Local State Variables
     */

    @Test
    public void testSicpWithdrawGlobal() throws ParsingException {
        fromString("(define balance 100)").eval(env);
        
        fromString("(define (withdraw amount)" +
                   "      (if (>= balance amount)" +
                   "          (begin (set! balance (- balance amount))" +
                   "                 balance)" +
                   "          \"Insufficient funds\"))").eval(env);

        assertEquals((int)fromString("(withdraw 25)").eval(env), 75);
        assertEquals((int)fromString("(withdraw 25)").eval(env), 50);
        assertEquals((String)fromString("(withdraw 60)").eval(env), "Insufficient funds");
        assertEquals((int)fromString("(withdraw 15)").eval(env), 35);
    }

    @Test
    public void testSicpWithdrawGlobalLambda() throws ParsingException {
        fromString("(define balance 100)").eval(env);
        
        fromString("(define withdraw (lambda (amount)" +
                   "      (if (>= balance amount)" +
                   "          (begin (set! balance (- balance amount))" +
                   "                 balance)" +
                   "          \"Insufficient funds\")))").eval(env);

        assertEquals(75, (int)fromString("(withdraw 25)").eval(env));
        assertEquals(50, (int)fromString("(withdraw 25)").eval(env));
        assertEquals("Insufficient funds", (String)fromString("(withdraw 60)").eval(env));
        assertEquals(35, (int)fromString("(withdraw 15)").eval(env));
    }


    /* esempio da sicp (pagina 300 della versione texinfo, pagina 328 del pdf
     */
    @Test
    public void testSicpWithdrawLocal() throws ParsingException {
        fromString("(define new-withdraw " +
                   "  (let ((balance 100)) " +
                   "    (lambda (amount) " +
                   "      (if (>= balance amount) " +
                   "          (begin (set! balance (- balance amount)) " +
                   "                 balance) " +
                   "          \"Insufficient funds\")))) " +
                   "        assertTrue(true); ").eval(env);

        assertEquals(75, (int)fromString("(new-withdraw 25)").eval(env));
        assertEquals(50, (int)fromString("(new-withdraw 25)").eval(env));
        assertEquals("Insufficient funds", (String)fromString("(new-withdraw 60)").eval(env));
        assertEquals(35, (int)fromString("(new-withdraw 15)").eval(env));

        assertNull(env.lookup(new LispSymbol("balance")));
    }

    /* pagina 301, (329 del pdf)
     */
    @Test
    public void testSicpMakeWithdraw() throws ParsingException {
        fromString("(define (make-withdraw balance) " +
                   " (lambda (amount) " +
                   "  (if (>= balance amount) " +
                   "      (begin (set! balance (- balance amount)) " +
                   "       balance) " +
                   "       \"Insufficient funds\"))) ").eval(env);
        
        fromString("(define W1 (make-withdraw 100))").eval(env);
        fromString("(define W2 (make-withdraw 100))").eval(env);

        assertEquals(50, (int)fromString("(W1 50)").eval(env));
        assertEquals(30, (int)fromString("(W2 70)").eval(env));
        assertEquals("Insufficient funds", (String)fromString("(W2 40)").eval(env));
        assertEquals(10, (int)fromString("(W1 40)").eval(env));
    }

    /* pagina 301, (329 del pdf)
     */
    @Test
    public void testSicpMakeWithdrawLambda() throws ParsingException {
        fromString("(define make-withdraw " +
                   "(lambda (balance) " +
                   " (lambda (amount) " +
                   "  (if (>= balance amount) " +
                   "      (begin (set! balance (- balance amount)) " +
                   "       balance) " +
                   "       \"Insufficient funds\")))) ").eval(env);
        
        fromString("(define W1 (make-withdraw 100))").eval(env);
        fromString("(define W2 (make-withdraw 100))").eval(env);

        assertEquals(50, (int)fromString("(W1 50)").eval(env));
        assertEquals(30, (int)fromString("(W2 70)").eval(env));
        assertEquals("Insufficient funds", (String)fromString("(W2 40)").eval(env));
        assertEquals(10, (int)fromString("(W1 40)").eval(env));
    }

}
