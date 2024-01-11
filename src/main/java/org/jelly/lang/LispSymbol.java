package org.jelly.lang;

public class LispSymbol {
    private String name;
    public LispSymbol(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "symbol(" + name + ")";
    }

    @Override
    public int hashCode() {
        return ~(name.hashCode());
    }

    // VERE EMPORTANT
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LispSymbol ls) {
            return name.equals(ls.name);
        }
        return false;
    }
}
