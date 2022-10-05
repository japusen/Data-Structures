package deque;

import edu.princeton.cs.algs4.Stopwatch;

public class TimeArrayDeque {
    private static void printTimingTable(ArrayDeque<Integer> Ns,
                                         ArrayDeque<Double> times,
                                         ArrayDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        //timeAdd();
        timeRemove();
    }

    public static void timeAdd() {
        ArrayDeque<Integer> n = new ArrayDeque<>();
        ArrayDeque<Double> times = new ArrayDeque<>();

        for (int i = 1000; i <= 128000; i *= 2) {
            n.addLast(i);
            ArrayDeque<Integer> test = new ArrayDeque<>();
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
        ArrayDeque<Integer> n = new ArrayDeque<>();
        ArrayDeque<Double> times = new ArrayDeque<>();

        for (int i = 1000; i <= 128000; i *= 2) {
            n.addLast(i);
            ArrayDeque<Integer> test = new ArrayDeque<>();

            for (int iter = 0; iter < i; iter++) {
                test.addFirst(iter);
            }

            Stopwatch sw = new Stopwatch();

            for (int iter = 0; iter < i; iter++) {
                //test.removeFirst();
                test.removeLast();
            }

            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }

        printTimingTable(n, times, n);
    }
    
}
