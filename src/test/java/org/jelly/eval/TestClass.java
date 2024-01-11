package org.jelly.eval;

public class TestClass {
    public String testMeth() {
        return "string";
    }
    public String testMeth(String arg) {
        return "string" + arg;
    }
    public String testMeth(String one, Integer two) {
        return one + "string" + two.toString();
    }

    public Integer mixal(int a, Integer b, int c) {
        b += (a + c);
        return b;
    }
}
