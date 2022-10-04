package deque;

import java.util.Iterator;

public class ArrayDeque<T> {

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
    public void addLast(T item) {
        array[nextLast] = item;
        incrementLastIndex();
        size++;

        //rethink condition
        if (size == array.length) {
            resize(array.length * 2);
        }
    }

    /** Returns true if deque is empty, false otherwise.*/
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, separated by a space.
     *  Once all the items have been printed, print out a new line. */
    public void printDeque() {
        for (int i = 0; i < size; i += 1) {
            System.out.print(array[adjustedIndex(i)] + " ");
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        if ((size < array.length / 4) && (size >= 16)) {
            resize(array.length / 2);
        }

        size--;
        incrementFirstIndex();
        return array[nextFirst];
    }

    /** Removes and returns the item at the back of the deque. If no such item exists, returns null. */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        if ((size < array.length / 4) && (size >= 16)) {
            resize(array.length / 2);
        }

        size--;
        decrementLastIndex();
        return array[nextLast];
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    public T get(int index) {
        if (isEmpty() || index >= size) {
            return null;
        }

        return array[adjustedIndex(index)];
    }

    /** The Deque objects we’ll make are iterable (i.e. Iterable<T>)
     * so we must provide this method to return an iterator. */
    public Iterator<T> iterator() {
        // TODO:
        return null;
    }

    /** Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order. (ADDED 2/12: You’ll need to use the instance of keywords for this. */
    public boolean equals(Object o) {
        // TODO:
        return false;
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
