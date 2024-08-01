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

    public void methodTakingInt(int i) {
        System.out.println(i);
    }

    public void methodTakingBoxedInteger(Integer i) {
        System.out.println(i);
    }

    public void methodTakingIntAndString(int i, String s) {
        System.out.println(i);
        System.out.println(s);
    }

    public void methodTakingBoxedIntegerAndString(Integer i, String s) {
        System.out.println(i);
        System.out.println(s);
    }

    public void methodTakingTwoInts(int i, int j) {
        System.out.println(i);
        System.out.println(j);
    }

    public void methodTakingIntAndBoxedInteger(int i, Integer j) {
        System.out.println(i);
        System.out.println(j);
    }

    public void methodTakingTwoBoxedIntegers(Integer i, Integer j) {
        System.out.println(i);
        System.out.println(j);
    }

    public int add(Integer y) {
        this.x += y;
        return this.x;
    }
    public int sub(Integer y) {
        this.x -=y;
        return this.x;
    }

    public int canThrow(int i) throws Exception {
        if(i == 0) {
            throw new Exception("exception");
        }
        else return x;
    }

    public static int canThrowStatic(int i) throws Exception {
        if(i == 0) {
            throw new Exception("exception");
        }
        else return i;
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
