package bearmaps;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

public class ArrayHeapMinPQTest {

    public static void main(String[] args) {

    }

    @Test
    public void testrandomp() {
        double item, priority, c;
        ArrayHeapMinPQ<Double> demo = new ArrayHeapMinPQ<>();
        for (int i = 0; i < 100000; i++) {
            item = StdRandom.uniform(-1000.0, 1000.0);
            priority = StdRandom.uniform(-1000.0, 1000.0);
            c = StdRandom.uniform(-1000.0, 1000.0);
            if (c > 500) {
                demo.add(item, priority);
                System.out.println("added" + "at " + i + " round");
            } else if (c > 0 && c < 500) {
                if (demo.size() != 0) {
                    demo.removeSmallest();
                    System.out.println("smallest removed" + "at " + i + " round");
                }
            } else if (c < 0 && c > -500) {
                System.out.println("did nothing " + "at " + i + " round");
            } else {
                if (demo.size() != 0) {
                    System.out.println("the smallest is "
                            + demo.getSmallest() + "at " + i + " round");
                    System.out.println("the size is " + demo.size() + "at " + i + " round");
                }
            }
        }
    }

    @Test
    public void timingtest() {
        ArrayHeapMinPQ<Double> demo = new ArrayHeapMinPQ<>();
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 1000000; i += 1) {
            double d = i;
            demo.add(d, d);
        }
        System.out.println("Total time elapsed: " + sw.elapsedTime() + " seconds.");


        Stopwatch b = new Stopwatch();
        for (int j = 0; j < 1000000; j += 1) {
            double d = j;
            demo.contains(d);
        }
        System.out.println("Total time elapsed: " + b.elapsedTime() + " seconds.");

        Stopwatch c = new Stopwatch();
        for (int j = 0; j < 1000000; j += 1) {
            double d = j;
            demo.size();
        }
        System.out.println("Total time elapsed: " + c.elapsedTime() + " seconds.");


        Stopwatch d = new Stopwatch();
        for (int j = 0; j < 1000000; j += 1) {
            double a = j;
            demo.changePriority(a, a + 1);
        }
        System.out.println("Total time elapsed: " + d.elapsedTime() + " seconds.");


        Stopwatch e = new Stopwatch();
        for (int j = 0; j < 1000000; j += 1) {
            double a = j;
            demo.removeSmallest();
        }
        System.out.println("Total time elapsed: " + e.elapsedTime() + " seconds.");


        NaiveMinPQ<Double> haha = new NaiveMinPQ<>();
        for (int j = 0; j < 1000000; j += 1) {
            double a = j;
            haha.add(a, a);
        }
        Stopwatch f = new Stopwatch();
        for (int j = 0; j < 1000000; j += 1) {
            double a = j;
            haha.removeSmallest();
        }
        System.out.println("Total time elapsed: " + f.elapsedTime() + " seconds.");
    }

}
