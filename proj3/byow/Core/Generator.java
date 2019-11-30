package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

import static byow.TileEngine.Tileset.*;

/* This is a comment */
public class Generator {
    /* This is a comment */
    static final int WIDTH = 80;
    /* This is a comment */
    static final int HEIGHT = 30;
    static Random _RANDOM;
    private static long SEED;
    /*Records the total number of rooms*/

    public static TETile[][] randomgenerator(long index) {

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        ArrayList<Room> rooms = new ArrayList<>();
        SEED = index;
        _RANDOM = new Random(SEED);
        int num = 0;

        /* fill the world with nothing */
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                world[i][j] = Tileset.NOTHING;
            }
        }

        /* build up set of rooms */
        Room first = new Room(rooms, num);
        rooms.add(first);
        first.addToWorld(world);
        num++;
        //centers.add(first.center());
        Room lastroom = first;
        /* generate random rooms */

        while (num < 10) {
            Room r = new Room(rooms, num);
            rooms.add(r);
            //centers.add(r.center());
            r.addToWorld(world);
            num++;

            /* buld hallways */
            Position hallway = Room.generatehall(lastroom, r);
            //System.out.println("hallway" + hallway.x + " " + hallway.y);
            Position curp = new Position(hallway.x, lastroom.getRighttop().y);
            //System.out.println("curp" + curp.x + " " + curp.y);
            Position newp = new Position(r.getRighttop().x, hallway.y);
            //System.out.println("newp" + newp.x + " " + newp.y);

            Position hallwaycopy = new Position(hallway.x, hallway.y);
            Room hallway1 = new Room(curp, hallway);
            Room hallway2 = new Room(newp, hallwaycopy);

            rooms.add(hallway1);
            rooms.add(hallway2);
            hallway1.addToWorld(world);
            hallway2.addToWorld(world);

            lastroom = r;
        }


        /*

        for (Room ro: rooms) {
            for (int x = 0; x <= 10 &&!ro.connected; x++) {
                Room.connect(ro, world);
            }

        }
        /*
        int i = 1;
        for(Room r : rooms) {
            System.out.println(i + " connected =  " + r.connected);
            i++;
        }
        /*for(Room alone: rooms) {
            if (!alone.connected) {
                Room.suiyuanconnect(alone, world);
            }
        }*/

        generateWalls(world);
        //generatePlayer(world);
        return world;
    }

    /* build walls */
    public static void generateWalls(TETile[][] world) {
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                if (Room.isWall(i, j, world)) {
                    world[i][j] = WALL;
                }
            }
        }
    }

    public static void generatePlayer(TETile[][] world) {
        int x = _RANDOM.nextInt(5) + 1;
        int y = _RANDOM.nextInt(HEIGHT - 2) + 1;
        while (world[x][y] != FLOOR) {
            x = _RANDOM.nextInt(5) + 1;
            y = _RANDOM.nextInt(HEIGHT - 2) + 1;
        }
        world[x][y] = AVATAR;
    }

    public static Position player(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    world[i][j] = Tileset.AVATAR;
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public static Position boss(TETile[][] world) {
        for (int i = HEIGHT - 2; i > 1; i--) {
            for (int j = WIDTH - 2; j > 1; j--) {
                if (world[j][i] == Tileset.FLOOR) {
                    world[j][i] = Tileset.BOSS;
                    return new Position(j, i);
                }
            }
        }
        return null;
    }

    public static Position lockdoor(TETile[][] world) {
        for (int i = WIDTH - 2; i > 1; i--) {
            for (int j = HEIGHT - 2; j > 1; j--) {
                if (world[i][j] == Tileset.WALL) {
                    if (world[i - 1][j] == Tileset.FLOOR || world[i + 1][j] == Tileset.FLOOR
                            || world[i][j - 1] == Tileset.FLOOR
                            || world[i][j + 1] == Tileset.FLOOR) {
                        world[i][j] = Tileset.LOCKED_DOOR;
                        return new Position(i, j);
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList<Position> denero(TETile[][] world) {
        ArrayList<Position> deneros = new ArrayList<>();
        int i = 0;
        while (deneros.size() < 6) {
            Position p = Position.randomposition();
            if (world[p.x][p.y] == Tileset.FLOOR) {
                world[p.x][p.y] = Tileset.DENERO;
                deneros.add(p);
                i++;
            }
        }
        return deneros;
    }

    public static ArrayList<Position> frenkel(TETile[][] world) {
        ArrayList<Position> frenkels = new ArrayList<>();
        int i = 0;
        while (frenkels.size() < 3) {
            Position p = Position.randomposition();
            if (world[p.x][p.y] == Tileset.FLOOR) {
                world[p.x][p.y] = Tileset.FRENKEL;
                frenkels.add(p);
                i++;
            }
        }
        return frenkels;
    }

    /*

    public static void main(String[] args) {

        TERenderer myworld = new TERenderer();
        myworld.initialize(WIDTH, HEIGHT);

        TETile[][] world = randomgenerator(30);

        myworld.renderFrame(world);

    }
    */


}
