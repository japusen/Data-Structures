package deque;

import java.util.Iterator;

public interface Deque<Item> {
    void addFirst(Item x);
    void addLast(Item y);
    Item removeFirst();
    Item removeLast();
    Item get(int i);
    int size();
    void printDeque();
    Iterator<Item> iterator();
    boolean equals(Object o);


    default boolean isEmpty() {
        return size() == 0;
    }
}
