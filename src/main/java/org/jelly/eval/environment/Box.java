package org.jelly.eval.environment;

public class Box {
    private Object contained;
    public Box(Object contained) {
        this.contained = contained;
    }
    public synchronized Object get() {
        return contained;
    }
    public synchronized void set(Object newObject) {
        contained = newObject;
    }

    @Override
    public synchronized int hashCode() {
        return contained.hashCode();
    }

    @Override
    public synchronized boolean equals(Object o) {
        if(o instanceof Box b) {
            return b.contained.equals(contained);
        }
        return false;
    }
}
