package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;

import static byow.Core.Generator.*;
import static byow.TileEngine.Tileset.FLOOR;
import static byow.TileEngine.Tileset.NOTHING;

public class Room {
    /* Hint: HallWay : r - l = 1 */
    private Position leftbot;
    private Position righttop;

    public Position getLeftbot() {
        return leftbot;
    }

    public Position getRighttop() {
        return righttop;
    }

    /* Creates a room given the leftbottom & righttop points. */
    public Room(Position p, Position q) {
        Position.validate(p, q);
        leftbot = p;
        righttop = q;
    }

    /* Randomly creates a room. */
    public Room(ArrayList<Room> rooms, int num) {
        leftbot = Position.randomposition();
        righttop = Position.randomposition(leftbot);
        if (num == 0) {
            return;
        }
        while (inRooms(rooms, this)) {
            leftbot = Position.randomposition();
            righttop = Position.randomposition(leftbot);
        }

    }

    public static Position generatehall(Room rooma, Room roomb) {
        Position p1 = rooma.leftbot;
        Position q1 = rooma.righttop;
        Position p2 = roomb.leftbot;
        Position q2 = roomb.righttop;
        Position answer = new Position(p1.x
                + _RANDOM.nextInt(Math.abs(q1.x - p1.x) + 1),
                p2.y + _RANDOM.nextInt(Math.abs(q2.y - p2.y) + 1));
        return answer;
    }

    public static boolean isWall(int i, int j, TETile[][] world) {
        if (world[i][j] != NOTHING) {
            return false;
        }
        for (int k = 0; k < 9; k++) {
            if (helper(i, j, k, world) == FLOOR) {
                return true;
            }
        }
        return false;
    }

    /* Helps the method above*/
    private static TETile helper(int i, int j, int k, TETile[][] world) {
        if (k == 0 && i != 0 && j != 0) {
            return world[i - 1][j - 1];
        }
        if (k == 1 && i != 0) {
            return world[i - 1][j];
        }
        if (k == 2 && i != 0 && j != HEIGHT - 1) {
            return world[i - 1][j + 1];
        }
        if (k == 3 && j != 0) {
            return world[i][j - 1];
        }
        if (k == 4) {
            return world[i][j];
        }
        if (k == 5 && j != HEIGHT - 1) {
            return world[i][j + 1];
        }
        if (k == 6 && i != WIDTH - 1 && j != 0) {
            return world[i + 1][j - 1];
        }
        if (k == 7 && i != WIDTH - 1) {
            return world[i + 1][j];
        }
        if (k == 8 && i != WIDTH - 1 && j != HEIGHT - 1) {
            return world[i + 1][j + 1];
        }
        return NOTHING;
    }

    public static Position randomPositionGenerator(Room r) {
        int x = r.leftbot.x + _RANDOM.nextInt(r.righttop.x - r.leftbot.x);
        int y = r.leftbot.y + _RANDOM.nextInt(r.righttop.y - r.leftbot.y);
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
        Room p = (Room) o;
        return (this.leftbot == p.leftbot) && (this.righttop == p.righttop);
    }

    @Override
    public int hashCode() {
        return 1;
    }


    /* Check all rooms to see if a point is in a room. */
    private boolean inRooms(ArrayList<Room> r, Room neww) {
        for (Room hey : r) {
            if (inRoom(hey, neww)) {
                return true;
            }
        }
        return false;
    }

    /*
    private static Room newHall(Position a, Position b) {
        return new Room(a, b);
    }

    /*
    public Position center(){
        int x = (leftbot.x + righttop.x) / 2;
        int y = (leftbot.y + righttop.y) / 2;
        return new Position(x, y);
    }

    private boolean centerinY(Room r) {
        if (this.center().y >= r.leftbot.y && this.center().y <= r.righttop.y) {
            return true;
        }
        return false;
    }

    private boolean centerinX(Room r) {
        if (this.center().x >= r.leftbot.x && this.center().x <= r.righttop.x) {
            return true;
        }
        return false;
    }
    */

    /* Check to see if a point is in this room. */
    private boolean inRoom(Room old, Room neww) {
        if (neww.leftbot.x > old.leftbot.x - 3
                && neww.leftbot.x < old.righttop.x + 3
                && neww.leftbot.y > old.leftbot.y - 3
                && neww.leftbot.y < old.righttop.y + 3) {
            return true;
        }
        if (neww.righttop.x > old.leftbot.x - 3
                && neww.righttop.x < old.righttop.x + 3
                && neww.righttop.y > old.leftbot.y - 3
                && neww.righttop.y < old.righttop.y + 3) {
            return true;
        }
        if (neww.leftbot.x > old.leftbot.x - 3
                && neww.leftbot.x < old.righttop.x + 3
                && neww.righttop.y > old.leftbot.y - 3
                && neww.righttop.y < old.righttop.y + 3) {
            return true;
        }
        if (neww.righttop.x > old.leftbot.x - 3
                && neww.righttop.x < old.righttop.x + 3
                && neww.leftbot.y > old.leftbot.y - 3
                && neww.leftbot.y < old.righttop.y + 3) {
            return true;
        }
        return false;
    }
    /*
    public static void connect (Room a, TETile[][] world) {
        int bbb = RANDOM.nextInt(rooms.size());
        while (rooms.get(bbb).equals(a)) {
            bbb = RANDOM.nextInt(rooms.size());
        }
        Room b = rooms.get(bbb);
        Position aa = randomPositionGenerator(a);
        Position bb = randomPositionGenerator(b);
        while (aa.equals(bb)) {
            bb = randomPositionGenerator(b);
        }
        if (aa.x == bb.x || aa.y == bb.y) {
            Room h = newHall(aa, bb);
            h.addToWorld(world);
            a.connected = true;
            b.connected = true;
        }
    }
    */

    public void addToWorld(TETile[][] world) {
        for (int i = leftbot.x; i <= righttop.x; i++) {
            for (int j = leftbot.y; j <= righttop.y; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
    }




/*
    public static void connect(Room a, Room b, TETile[][] world){
        if (a.centerinX(b)) {
            Position temp = new Position(a.center().x, b.righttop.y);
            Room hall = a.newHall(a.center(), temp);
            hall.addToWorld(world);
        }
        if (a.centerinY(b)) {
            Position temp = new Position(b.righttop.x, a.center().y);
            Room hall = a.newHall(a.center(), temp);
            hall.addToWorld(world);
        }
    }
*/


}

