package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;


public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();
        StringBuilder message = new StringBuilder("\n");

        int N = 1000000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 7);

            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                student.addFirst(randVal);
                solution.addFirst(randVal);
                message.append("addFirst(" + randVal + ")\n");

            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                student.addLast(randVal);
                solution.addLast(randVal);
                message.append("addLast(" + randVal + ")\n");

            } else if (operationNumber == 2) {
                //remove first
                if (student.size() == 0) {
                    continue;
                } else {
                    Integer studentLast = student.removeFirst();
                    Integer solutionLast = solution.removeFirst();
                    message.append("removeFirst()\n");
                    assertEquals(message.toString(), studentLast, solutionLast);
                }

            } else if (operationNumber == 3) {
                //get
                if (student.size() == 0) {
                    continue;
                }
                int index = StdRandom.uniform(0, solution.size());
                Integer studentItem = student.get(index);
                Integer solutionItem = solution.get(index);
                message.append("get(" + index + ")\n");
                assertEquals(message.toString(), studentItem, solutionItem);

            } else if (operationNumber == 4) {
                //removeLast
                if (student.size() == 0) {
                    continue;
                } else {
                    Integer studentLast = student.removeLast();
                    Integer solutionLast = solution.removeLast();
                    message.append("removeLast()\n");
                    assertEquals(message.toString(), studentLast, solutionLast);
                }

            } else if (operationNumber == 5) {
                // size
                int studentSize = student.size();
                int solutionSize = solution.size();
                message.append("size()\n");
                assertEquals(message.toString(), studentSize, solutionSize);

            } else {
                //isEmpty
                boolean studentEmpty = student.isEmpty();
                boolean solutionEmpty = solution.isEmpty();
                message.append("isEmpty()\n");
                assertEquals(message.toString(), studentEmpty, solutionEmpty);

            }
        }
    }
}
