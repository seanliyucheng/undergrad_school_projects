package bearmaps;

import java.util.ArrayList;
import java.util.List;

public class NaivePointSet implements PointSet {

    private List<Point> allpoints;

    public NaivePointSet(List<Point> points) {
        allpoints = new ArrayList<>();
        for (Point p : points) {
            allpoints.add(p);
        }
    }

    public static void main(String[] args) {
        Point p1 = new Point(1.1, 2.2); // constructs a Point with x = 1.1, y = 2.2
        Point p2 = new Point(3.3, 4.4);
        Point p3 = new Point(-2.9, 4.2);

        NaivePointSet nn = new NaivePointSet(List.of(p1, p2, p3));
        Point ret = nn.nearest(3.0, 4.0); // returns p2
        ret.getX(); // evaluates to 3.3
        ret.getY(); // evaluates to 4.4
        System.out.println(ret.getX());
        System.out.println(ret.getY());
    }

    @Override
    public Point nearest(double x, double y) {
        Point best = allpoints.get(0);
        Point goal = new Point(x, y);
        for (Point p : allpoints) {
            double curdistance = Point.distance(p, goal);
            if (curdistance < Point.distance(best, goal)) {
                best = p;
            }
        }
        return best;
    }
}
