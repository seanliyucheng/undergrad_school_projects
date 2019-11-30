package byow.Core;

import static byow.Core.Generator._RANDOM;

public class Position {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    int x;
    int y;


    public Position(int xx, int yy) {
        x = xx;
        y = yy;
    }

    /* Validates a left Pos & a right Pos. If not valid, then flip the points. */
    public static void validate(Position leftbot, Position righttop) {
        Position tmp = new Position(1, 1);
        if (leftbot.x > righttop.x) {
            tmp.x = righttop.x;
            righttop.x = leftbot.x;
            leftbot.x = tmp.x;
        }
        if (leftbot.y > righttop.y) {
            tmp.y = righttop.y;
            righttop.y = leftbot.y;
            leftbot.y = tmp.y;
        }
    }

    /* Generates a random Position. */
    public static Position randomposition() {
        int x = _RANDOM.nextInt(WIDTH - 3) + 1;
        int y = _RANDOM.nextInt(HEIGHT - 3) + 1;
        return new Position(x, y);

    }

    /* Generates a random position to form a valid room with the given point p. */
    public static Position randomposition(Position p) {
        int x = p.x + 1 + _RANDOM.nextInt(10);
        int y = p.y + 1 + _RANDOM.nextInt(10);
        if (x >= WIDTH - 1) {
            x = p.x + 1;
        }
        if (y >= HEIGHT - 1) {
            y = p.y + 1;
        }

        return new Position(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Position p = (Position) o;
        return (this.x == p.x) && (this.y == p.y);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public static double distTo(Position a, Position b) {
        return Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2);
    }



}

