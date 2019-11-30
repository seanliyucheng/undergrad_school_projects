package bearmaps;


import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class KDTreeTest {
    private static Random r = new Random(500);

    private Point randomPoint() {
        double x = r.nextDouble();
        double y = r.nextDouble();
        return new Point(x, y);
    }

    private List<Point> randomPoints(int N) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            points.add(randomPoint());
        }
        return points;
    }

    @Test
    public void randomizedtest() {
        List<Point> points = randomPoints(100000);
        NaivePointSet hey = new NaivePointSet(points);
        KDTree kd = new KDTree(points);

        List<Point> queries200 = randomPoints(20000);
        for (Point p : queries200) {
            Point expected = hey.nearest(p.getX(), p.getY());
            Point actual = kd.nearest(p.getX(), p.getY());
            assertEquals(expected, actual);
        }
    }

    @Test
    public void speedtest() {
        List<Point> points = randomPoints(100000);
        KDTree kd = new KDTree(points);

        Stopwatch sw = new Stopwatch();
        List<Point> queries200 = randomPoints(10000);
        for (Point p : queries200) {
            Point actual = kd.nearest(p.getX(), p.getY());
        }
        System.out.println("own solution " + sw.elapsedTime());

        List<Point> p2 = randomPoints(100000);
        NaivePointSet hey = new NaivePointSet(p2);

        Stopwatch sw2 = new Stopwatch();
        List<Point> q2 = randomPoints(10000);
        for (Point p : q2) {
            Point expected = hey.nearest(p.getX(), p.getY());
        }
        System.out.println("naive: " + sw2.elapsedTime());


    }


}
