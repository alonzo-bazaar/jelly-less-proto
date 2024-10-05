package org.jelly.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class PeekingIterator<T> implements Iterator<T> {
    private final List<T> queue = new ArrayList<>();
    private final Iterator<T> inner;

    public PeekingIterator(Iterator<T> inner) {
        this.inner = inner;
    }

    public Optional<T> peek() {
        // should be idempotent
        if(queue.isEmpty() && !(inner.hasNext()))
            return Optional.empty();
        else if(queue.isEmpty())
            queue.addLast(inner.next());
        return Optional.of(queue.getFirst());
    }

    public Optional<T> peekAhead(int skipped) {
        // should be idempotent
        // peek() == peekN(0)
        while(queue.size() < (skipped + 1)) {
            if(inner.hasNext()) {
                queue.addLast(inner.next());
            }
            else
                return Optional.empty();
        }
        return Optional.of(queue.get(skipped));
    }

    @Override
    public T next() {
        if(queue.isEmpty()) {
            return inner.next();
        }
        return queue.removeFirst();
    }

    @Override
    public boolean hasNext() {
        return !(queue.isEmpty()) || inner.hasNext();
    }
}
