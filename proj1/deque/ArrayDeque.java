package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private T[] array;
    private int size;
    private int nextFirst;
    private int nextLast;


    /** Constructor to make an empty list */
    public ArrayDeque() {
        array = (T []) new Object[8];
        size = 0;
        nextLast = 0;
        nextFirst = array.length - 1;
    }

    /** Adds an item of type T to the front of the deque. You can assume that item is never null. */
    @Override
    public void addFirst(T item) {
        array[nextFirst] = item;
        decrementFirstIndex();
        size++;

        //rethink condition
        if (size == array.length) {
            resize(array.length * 2);
        }
    }

    /** Adds an item of type T to the back of the deque. You can assume that item is never null. */
    @Override
    public void addLast(T item) {
        array[nextLast] = item;
        incrementLastIndex();
        size++;

        //rethink condition
        if (size == array.length) {
            resize(array.length * 2);
        }
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
        for (int i = 0; i < size; i += 1) {
            System.out.print(array[adjustedIndex(i)] + " ");
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null. */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        size--;
        if ((size < array.length / 4) && (size >= 16)) {
            resize(array.length / 2);
        }
        incrementFirstIndex();
        return array[nextFirst];
    }

    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null. */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        size--;
        if ((size < array.length / 4) && (size >= 16)) {
            resize(array.length / 2);
        }
        decrementLastIndex();
        return array[nextLast];


    }


    /** Gets the item at the given index, where 0 is the front,
     * 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    @Override
    public T get(int index) {
        if (isEmpty() || index >= size) {
            return null;
        }

        return array[adjustedIndex(index)];
    }

    /** The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator. */
    @Override
    public Iterator<T> iterator() {
        return new ADequeIterator();
    }

    private class ADequeIterator implements Iterator<T> {
        private int pos;

        ADequeIterator() {
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
     * (as governed by the generic T’s equals method) in the same order.
     * (ADDED 2/12: You’ll need to use the instance of keywords for this.) */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Deque)) {
            return false;
        }

        ArrayDeque<T> comp = (ArrayDeque<T>) o;

        if (this == comp) {
            return true;
        }

        if (this.size() != comp.size()) {
            return false;
        }

        Iterator<T> compIter = comp.iterator();

        for (T item : this) {
            if (!item.equals(compIter.next())) {
                return false;
            }
        }

        return true;
    }

    /** Increments the index of nextLast and accounts for looping to the other side of the array*/
    private void incrementLastIndex() {
        if (nextLast == array.length - 1) {
            nextLast = 0;
        } else {
            nextLast++;
        }
    }

    /** Decrements the index of nextLast and accounts for looping to the other side of the array*/
    private void decrementLastIndex() {
        if (nextLast == 0) {
            nextLast = array.length - 1;
        } else {
            nextLast--;
        }
    }

    /** Increments the index of nextFirst and accounts for looping to the other side of the array*/
    private void incrementFirstIndex() {
        if (nextFirst == array.length - 1) {
            nextFirst = 0;
        } else {
            nextFirst++;
        }
    }

    /** Decrements the index of nextFirst and accounts for looping to the other side of the array*/
    private void decrementFirstIndex() {
        if (nextFirst == 0) {
            nextFirst = array.length - 1;
        } else {
            nextFirst--;
        }
    }

    /** Returns the index that represents the nth index in the circular array*/
    private int adjustedIndex(int n) {
        int start = nextFirst + 1;
        return (start + n) % array.length;
    }

    /** Resizes the array to capacity and adjusts the nextFirst and nextLast*/
    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            a[i] = array[adjustedIndex(i)];
        }
        nextLast = size;
        nextFirst = a.length - 1;
        array = a;
    }
}
