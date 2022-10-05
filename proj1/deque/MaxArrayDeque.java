package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> comparator;

    /** Creates a MaxArrayDeque with the given Comparator */
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }


    /** Returns the maximum element in the deque as governed by the previously given Comparator.
     * If the MaxArrayDeque is empty, simply return null */
    public T max() {
        return max(comparator);
    }

    /** Returns the maximum element in the deque as governed by the parameter Comparator c.
     * If the MaxArrayDeque is empty, simply return null. */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (T item : this) {
            if(c.compare(item, max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
