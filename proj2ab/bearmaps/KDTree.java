package bearmaps;

import java.util.List;

// Inspired by Prof. Hug's walkthrough videos

public class KDTree implements PointSet {

    private static final boolean HORIZONTAL = false;
    private static final boolean VERTICAL = true;
    private Node root;

    public KDTree(List<Point> points) {
        for (Point p : points) {
            root = add(p, root, HORIZONTAL);
        }
    }

    private Node add(Point p, Node n, boolean o) {
        if (n == null) {
            return new Node(p, o);
        }
        if (p.equals(n.p)) {
            return n;
        }
        int compare = comparepoints(p, n.p, o);
        if (compare < 0) {
            n.leftchild = add(p, n.leftchild, !o);
        } else if (compare >= 0) {
            n.rightchild = add(p, n.rightchild, !o);
        }
        return n;
    }

    private int comparepoints(Point a, Point b, boolean orientation) {
        if (orientation == HORIZONTAL) {
            return Double.compare(a.getX(), b.getX());
        } else {
            return Double.compare(a.getY(), b.getY());
        }
    }

    @Override
    public Point nearest(double x, double y) {
        Point goal = new Point(x, y);
        Node best = root;
        best = nearest(root, goal, best);
        return best.p;
    }

    private Node nearest(Node n, Point goal, Node best) {
        Node good = null;
        Node bad = null;
        Point contrastpoint = null;
        if (n == null) {
            return best;
        }
        if (Point.distance(n.p, goal) < Point.distance(best.p, goal)) {
            best = n;
        }
        if (n.orientation == HORIZONTAL) {
            if (goal.getX() < n.p.getX()) {
                good = n.leftchild;
                bad = n.rightchild;
            } else {
                good = n.rightchild;
                bad = n.leftchild;
            }
            contrastpoint = new Point(n.p.getX(), goal.getY());
        }
        if (n.orientation == VERTICAL) {
            if (goal.getY() < n.p.getY()) {
                good = n.leftchild;
                bad = n.rightchild;
            } else {
                good = n.rightchild;
                bad = n.leftchild;
            }
            contrastpoint = new Point(goal.getX(), n.p.getY());
        }
        best = nearest(good, goal, best);
        if (Point.distance(contrastpoint, goal) < Point.distance(best.p, goal)) {
            best = nearest(bad, goal, best);
        }
        return best;
    }

    private class Node {
        private Point p;
        private boolean orientation;
        private Node leftchild;
        private Node rightchild;

        Node(Point thep, boolean o) {
            p = thep;
            orientation = o;
        }

    }


}
