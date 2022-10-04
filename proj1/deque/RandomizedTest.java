package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class RandomizedTest {
    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> Linked = new LinkedListDeque<>();
        ArrayDeque<Integer> Array = new ArrayDeque<>();

        int N = 100000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);

            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                Linked.addLast(randVal);
                Array.addLast(randVal);

            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                Linked.addFirst(randVal);
                Array.addFirst(randVal);

            } else if (operationNumber == 2) {
                //removeLast
                if (Linked.size() == 0) {
                    continue;
                } else {
                    Integer LinkedLast = Linked.removeLast();
                    Integer ArrayLast = Array.removeLast();
                    assertEquals(LinkedLast, ArrayLast);
                }

            } else if (operationNumber == 3) {
                //remove first
                if (Linked.size() == 0) {
                    continue;
                } else {
                    Integer LinkedLast = Linked.removeFirst();
                    Integer ArrayLast = Array.removeFirst();
                    assertEquals(LinkedLast, ArrayLast);
                }

            } else if (operationNumber == 4) {
                // size
                int LinkedSize = Linked.size();
                int ArraySize = Array.size();
                assertEquals(LinkedSize, ArraySize);

            } else if (operationNumber == 5) {
                //isEmpty
                boolean LinkedEmpty = Linked.isEmpty();
                boolean ArrayEmpty = Array.isEmpty();
                assertEquals(LinkedEmpty, ArrayEmpty);

            } else if (operationNumber == 6) {
                //get
                if (Linked.size() == 0) {
                    continue;
                }
                Integer LinkedItem = Linked.get(0);
                Integer ArrayItem = Array.get(0);
                assertEquals(LinkedItem, ArrayItem);
            }
        }
    }
}
