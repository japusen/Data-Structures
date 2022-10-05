package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{
    private IntNode sentinel;
    private int size;

    public class IntNode {
        public IntNode prev;
        public T item;
        public IntNode next;

        public IntNode(T elem) {
            item = elem;
        }
    }


    /** Constructor to make an empty list */
    public LinkedListDeque() {
        size = 0;
        sentinel = new IntNode(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    /** Adds an item of type T to the front of the deque. You can assume that item is never null. */
    @Override
    public void addFirst(T item) {
        IntNode first = sentinel.next;
        IntNode newNode = new IntNode(item);
        newNode.prev = sentinel;
        newNode.next = first;
        first.prev = newNode;
        sentinel.next = newNode;
        size++;
    }

    /** Adds an item of type T to the back of the deque. You can assume that item is never null. */
    @Override
    public void addLast(T item) {
        IntNode last = sentinel.prev;
        IntNode newNode = new IntNode(item);
        last.next = newNode;
        newNode.prev = last;
        newNode.next = sentinel;
        sentinel.prev = newNode;
        size++;
    }

    /** Returns the number of items in the deque. */
    @Override
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     *  Once all the items have been printed, print out a new line. */
    @Override
    public void printDeque() {
        IntNode pointer = sentinel.next;

        while (pointer != sentinel) {
            System.out.print(pointer.item + " ");
            pointer = pointer.next;
        }

        System.out.println();
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        IntNode first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        size--;

        return first.item;
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        IntNode last = sentinel.prev;
        last.prev.next = sentinel;
        sentinel.prev = last.prev;
        size--;

        return last.item;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    @Override
    public T get(int index) {
        IntNode pointer = sentinel.next;
        int i = 0;

        while (pointer != sentinel) {
            if (i == index) {
                return pointer.item;
            }

            pointer = pointer.next;
            i++;
        }
        return null;
    }

    /** The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator. */
    @Override
    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }

    private class LLDequeIterator implements Iterator<T> {
        private int pos;

        public LLDequeIterator() {
            pos = 0;
        }
        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            T item = get(pos);
            pos += 1;
            return item;
        }
    }

    /** Returns whether the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as governed by the generic T’s equals method) in the same order. (ADDED 2/12: You’ll need to use the instance of keywords for this.) */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LinkedListDeque)) {
            return false;
        }

        LinkedListDeque<T> comp = (LinkedListDeque<T>) o;

        if (this == comp) {
            return true;
        }
        if (this.size() != comp.size()) {
            return false;
        }

        Iterator<T> compIter = comp.iterator();

        for(T item : this) {
            if(!item.equals(compIter.next())) {
                return false;
            }
        }

        return true;
    }
}
