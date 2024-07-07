package org.jelly.examples;

public class ShoppingListItem {
    // data class
    private String name;
    private double price;
    private int amount;

    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public int getAmount() {
        return amount;
    }

    public ShoppingListItem(String name, float price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }
}
