package org.jelly.examples;

import java.util.List;
import java.util.ArrayList;

import org.jelly.eval.runtime.JellyRuntime;
import org.jelly.lang.data.ConsList;
import org.jelly.utils.ConsUtils;

public class ShoppingListExample {
    public static void main(String args[]) {
        System.out.println("total price is : " + listTotal(exampleShoppingList()));
    }

    private static double listTotal(List<ShoppingListItem> list) {
        JellyRuntime jr = new JellyRuntime();

        jr.evalString("(define (item-name item) (call item \"getName\"))" +
                "(define (item-price item) (call item \"getPrice\"))" +
                "(define (item-amount item) (call item \"getAmount\"))");

        jr.evalString( "(define (list-cost lst)" +
                        "  (define (item-total item)" +
                        "      (* (item-price item) (item-amount item)))" +
                        "   (reduce + (map item-total lst) 0))");

        ConsList ll = ConsUtils.toCons(list);
        return (double)jr.call("list-cost", ll);
    }

    private static List<ShoppingListItem> exampleShoppingList() {
        List<ShoppingListItem> lst = new ArrayList<>(4);
        lst.addLast(new ShoppingListItem("fusilli", 1.50f, 2));
        lst.addLast(new ShoppingListItem("zucchine", 0.80f, 10));
        lst.addLast(new ShoppingListItem("latte", 2.00f, 2));
        lst.addLast(new ShoppingListItem("ventilatore", 25.00f, 1));
        return lst;
    }
}
