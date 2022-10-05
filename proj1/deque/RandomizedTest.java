package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class RandomizedTest {
    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> linked = new LinkedListDeque<>();
        ArrayDeque<Integer> array = new ArrayDeque<>();

        int N = 1000000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);

            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                linked.addLast(randVal);
                array.addLast(randVal);

            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                linked.addFirst(randVal);
                array.addFirst(randVal);

            } else if (operationNumber == 2) {
                //removeLast
                if (linked.size() == 0) {
                    continue;
                } else {
                    Integer linkedLast = linked.removeLast();
                    Integer arrayLast = array.removeLast();
                    assertEquals(linkedLast, arrayLast);
                }

            } else if (operationNumber == 3) {
                //remove first
                if (linked.size() == 0) {
                    continue;
                } else {
                    Integer linkedLast = linked.removeFirst();
                    Integer arrayLast = array.removeFirst();
                    assertEquals(linkedLast, arrayLast);
                }

            } else if (operationNumber == 4) {
                // size
                int linkedSize = linked.size();
                int arraySize = array.size();
                assertEquals(linkedSize, arraySize);

            } else if (operationNumber == 5) {
                //isEmpty
                boolean linkedEmpty = linked.isEmpty();
                boolean arrayEmpty = array.isEmpty();
                assertEquals(linkedEmpty, arrayEmpty);

            } else if (operationNumber == 6) {
                //get
                if (linked.size() == 0) {
                    continue;
                }
                Integer linkedItem = linked.get(0);
                Integer arrayItem = array.get(0);
                assertEquals(linkedItem, arrayItem);
            }
        }
    }
}
