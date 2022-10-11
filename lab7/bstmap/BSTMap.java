package bstmap;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private int size;
    private BSTNode root;

    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }

        /** Returns true if the BSTNode has a greater key than another BSTNode */
        boolean greaterThan(BSTNode other) {
            return key.compareTo(other.key) > 0;
        }

        /** Returns true if the BSTNode has a greater key than a given key */
        boolean greaterThan(K otherKey) {
            return key.compareTo(otherKey) > 0;
        }

        boolean equals(K key) {
            return this.key.compareTo(key) == 0;
        }

        boolean lessThan(BSTNode other) {
            return key.compareTo(other.key) < 0;
        }
    }

    /** Creates and empty map */
    public BSTMap() {
        size = 0;
        root = null;
    }

    /** Prints out BSTMap in order of increasing Key */
    public void printInOrder() {
        printHelper(root);
    }

    private void printHelper(BSTNode node) {
        if (node == null) {
            return;
        }

        printHelper(node.left);
        System.out.println(node.key);
        printHelper(node.right);
    }

    /** Removes all the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return containsHelper(root, key);
    }

    private boolean containsHelper(BSTNode node, K key) {
        if (node == null) {
            return false;
        }

        if (node.equals(key)) {
            return true;
        } else if (node.greaterThan(key)) {
            return containsHelper(node.left, key);
        } else {
            return containsHelper(node.right, key);
        }
    }

    /** Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return getHelper(root, key);
    }

    private V getHelper(BSTNode node, K key) {
        if (node == null) {
            return null;
        }

        if (node.equals(key)) {
            return node.value;
        } else if (node.greaterThan(key)) {
            return getHelper(node.left, key);
        } else {
            return getHelper(node.right, key);
        }
    }


    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /** Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            return;
        }

        BSTNode node = new BSTNode(key, value);
        root = insert(root, node);
        size += 1;
    }

    private BSTNode insert(BSTNode node, BSTNode next) {
        if (node == null) {
            return next;
        }

        if (node.greaterThan(next)) {
            node.left = insert(node.left, next);
        } else if (node.lessThan(next)) {
            node.right = insert(node.right, next);
        }

        return node;

    }

    /** Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    /** Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /** Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }
}
