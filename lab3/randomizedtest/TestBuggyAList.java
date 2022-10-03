package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> sample = new AListNoResizing<>();
        BuggyAList<Integer> tester = new BuggyAList<>();

        for (int i = 0; i < 3; i++) {
            sample.addLast(i);
            tester.addLast(i);
        }

        for (int i = 0; i < 3; i++) {
            assertEquals(sample.removeLast(), tester.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int buggySize = buggy.size();
                assertEquals(size, buggySize);
            } else if (operationNumber == 2) {
                if (L.size() == 0) {
                    continue;
                } else {
                    int last = L.getLast();
                    int buggyLast = buggy.getLast();
                    assertEquals(last, buggyLast);
                }
            } else {
                if (L.size() == 0) {
                    continue;
                } else {
                    int last = L.removeLast();
                    int buggyLast = buggy.removeLast();
                    assertEquals(last, buggyLast);
                }
            }
        }
    }
}
