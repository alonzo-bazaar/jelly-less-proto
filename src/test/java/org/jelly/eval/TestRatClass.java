package org.jelly.eval;

public class TestRatClass {
    // simple class on which to test foreign method calls
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

    public void thrower() throws Exception {
        throw new Exception("except");
    }

    public void runtimeThrower() throws RuntimeException {
        throw new RuntimeException("runtime except");
    }

    public int returningThrower(int n) throws Exception {
        if(n%2==0) {
            return n+2;
        }
        else throw new Exception("return except");
    }

    public int returningRuntimeThrower(int n) throws RuntimeException {
        if(n%2==0) {
            return n+2;
        }
        else throw new RuntimeException("runtime return except");
    }
}
