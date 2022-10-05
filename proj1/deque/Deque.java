package deque;

import java.util.Iterator;

public interface Deque<T> {
    void addFirst(T x);
    void addLast(T x);
    int size();
    void printDeque();
    T removeFirst();
    T removeLast();
    T get(int i);
    Iterator<T> iterator();
    boolean equals(Object o);

    default boolean isEmpty() {
        return size() == 0;
    }
}
