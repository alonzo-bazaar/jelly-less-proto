package org.jelly.lang.javaffi;

// small class to test foreign calls on
public class LabRat {
    private int x;
    public LabRat(int x) {
        this.x = x;
    }

    public LabRat(String s) {
        this.x = s.length();
    }

    public LabRat(int x, int y) {
        this.x = x;
        LabRat.counter = y;
    }

    public int add(Integer y) {
        this.x += y;
        return this.x;
    }
    public int sub(Integer y) {
        this.x -=y;
        return this.x;
    }

    public int get() {
        return this.x;
    }

    static int counter = 0;
    public static int upCounter() {
        return ++counter;
    }
    public static int getCounter() {
        return counter;
    }

    public static void resetStaticData() {
        counter = 0;
    }
}
