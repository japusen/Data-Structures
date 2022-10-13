package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int size;
    private final double maxLF;

    /** Constructors */
    public MyHashMap() {
        size = 0;
        maxLF = 0.75;
        setUp(16);
    }

    public MyHashMap(int initialSize) {
        size = 0;
        maxLF = 0.75;
        setUp(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        maxLF = maxLoad;
        setUp(initialSize);
    }

    /** Helper Methods */

    /** Creates the buckets hash table with the given size
     * and fills each bucket slot with an empty underlying Data Structure */
    private void setUp(int initialSize) {
        buckets = createTable(initialSize);
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
    }

    /** Returns true if the load factor exceeds the maximum */
    private boolean exceedsMaxLoadFactor() {
        return (1.0 * size / buckets.length) > maxLF;
    }

    /** Calculate hash index based on bucket length */
    private int hashIndex(K key, int length) {
        return Math.floorMod(key.hashCode(), length);
    }


    /** Returns a new table with mapped items reorganized with their new hash */
    private Collection<Node>[] resizedTable(int tableSize) {
        Collection<Node>[] temp = createTable(tableSize);
        for (int i = 0; i < temp.length; i++) {
            temp[i] = createBucket();
        }

        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int index = hashIndex(node.key, temp.length);
                temp[index].add(node);
            }
        }
        return temp;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }


    /** Removes all the mappings from this map. */
    @Override
    public void clear() {
        size = 0;
        setUp(buckets.length);
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        int index = hashIndex(key, buckets.length);

        for (Node item : buckets[index]) {
            if (item.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        int index = hashIndex(key, buckets.length);

        for (Node item : buckets[index]) {
            if (item.key.equals(key)) {
                return item.value;
            }
        }
        return null;
    }

    /** Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        int index = hashIndex(key, buckets.length);

        for (Node item : buckets[index]) {
            if (item.key.equals(key)) {
                item.value = value;
                return;
            }
        }

        buckets[index].add(createNode(key, value));
        size += 1;

        if (exceedsMaxLoadFactor()) {
            buckets = resizedTable(2 * buckets.length);
        }
    }

    /** Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();

        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                keys.add(node.key);
            }
        }
        return keys;
    }


    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    @Override
    public V remove(K key) {
        int index = hashIndex(key, buckets.length);
        Node node = null;

        for (Node item : buckets[index]) {
            if (item.key.equals(key)) {
                node = item;
                break;
            }
        }

        if (node != null) {
            buckets[index].remove(node);
            return node.value;
        } else {
            return null;
        }
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    @Override
    public V remove(K key, V value) {
        int index = hashIndex(key, buckets.length);
        Node node = null;

        for (Node item : buckets[index]) {
            if (item.key.equals(key) && item.value.equals(value)) {
                node = item;
                break;
            }
        }

        if (node != null) {
            buckets[index].remove(node);
            return node.value;
        } else {
            return null;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator<>();
    }

    private static class HashMapIterator<K> implements Iterator<K> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public K next() {
            return null;
        }
    }

}
