package deque;

import org.junit.Test;

import java.util.Comparator;
import java.util.Optional;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    @Test
    /** Use Int Max Comparator to get the largest integer in the list*/
    public void IntComparator() {
        Comparator<Integer> max = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return a - b;
            }
        };
        Comparator<Integer> min = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        };

        MaxArrayDeque<Integer> test = new MaxArrayDeque<>(max);
        for (int i = 0; i < 1000; i++) {
            test.addLast(i);
        }

        assertEquals("Int: max comparator", (Integer) 999, test.max());
        assertEquals("Int: min comparator", (Integer) 0, test.max(min));

    }

    @Test
    public void StringComparator() {
        Comparator<String> max = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        };
        Comparator<String> min = new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return b.length() - a.length();
            }
        };

        MaxArrayDeque<String> test = new MaxArrayDeque<>(max);
        test.addLast("hi");
        test.addLast("my");
        test.addLast("name");
        test.addLast("is");
        test.addLast("CS61B!!!!!!!!!!!!!");
        test.addLast("1");

        assertEquals("String: max comparator", "CS61B!!!!!!!!!!!!!", test.max());
        assertEquals("String: min comparator", "1", test.max(min));

    }
}
