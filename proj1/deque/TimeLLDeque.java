package deque;

import edu.princeton.cs.algs4.Stopwatch;

public class TimeLLDeque {
    private static void printTimingTable(LinkedListDeque<Integer> n,
                                         LinkedListDeque<Double> times,
                                         LinkedListDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < n.size(); i += 1) {
            int number = n.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", number, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAdd();
        //timeRemove();
    }

    public static void timeAdd() {
        LinkedListDeque<Integer> n = new LinkedListDeque<>();
        LinkedListDeque<Double> times = new LinkedListDeque<>();

        for (int i = 1000; i <= 128000; i *= 2) {
            n.addLast(i);
            LinkedListDeque<Integer> test = new LinkedListDeque<>();
            Stopwatch sw = new Stopwatch();

            for (int iter = 0; iter < i; iter++) {
                //test.addFirst(iter);
                test.addLast(iter);
            }

            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }

        printTimingTable(n, times, n);
    }

    public static void timeRemove() {
        LinkedListDeque<Integer> n = new LinkedListDeque<>();
        LinkedListDeque<Double> times = new LinkedListDeque<>();

        for (int i = 1000; i <= 128000; i *= 2) {
            n.addLast(i);
            LinkedListDeque<Integer> test = new LinkedListDeque<>();

            for (int iter = 0; iter < i; iter++) {
                test.addFirst(iter);
            }

            Stopwatch sw = new Stopwatch();

            for (int iter = 0; iter < i; iter++) {
                test.removeFirst();
                //test.removeLast();
            }

            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }

        printTimingTable(n, times, n);
    }

}
