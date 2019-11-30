package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;

public class World {
    private Position lockedDoor;
    private Position player;
    private Position boss;
    private ArrayList<Position> denero;
    private ArrayList<Position> frenkel;
    private TETile[][] world;

    public World(Position l, Position p, Position b,
                 ArrayList<Position> d, ArrayList<Position> f, TETile[][] w) {
        this.lockedDoor = l;
        this.player = p;
        this.boss = b;
        this.denero = d;
        this.frenkel = f;
        this.world = w;
    }

    public ArrayList<Position> denero() {
        return this.denero;
    }

    public Position boss() {
        return this.boss;
    }

    public ArrayList<Position> frenkel() {
        return this.frenkel;
    }


    public Position lockedDoor() {
        return this.lockedDoor;
    }

    public Position player() {
        return this.player;
    }

    public TETile[][] world() {
        return this.world;
    }

}
