package org.jelly.lang.data;

public record Symbol(String name) {
    @Override
    public String toString() {
        return "symbol(" + name + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol ls) {
            return name.equals(ls.name);
        }
        return false;
    }
}
