package org.jelly.utils;

public record Triple<A, B, C>(A first, B second, C third) {

    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
        return new Triple<A, B, C>(first, second, third);
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode() ^ third.hashCode();
    }
}