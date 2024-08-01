package org.jelly.utils;

public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A,B> Pair<A,B> of(A first, B second) {
        return new Pair<A,B>(first, second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }
}
