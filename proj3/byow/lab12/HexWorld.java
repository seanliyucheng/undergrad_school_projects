package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static class Position {
        public int x;
        public int y;

        public Position(int xx, int yy) {
            x = xx;
            y = yy;
        }
    }

    public static int hexRowWidth(int s, int i) {
        int I = i;
        if (i >= s) {
            I = 2 * s - 1 - I;
        }
        return s + 2 * I;
    }


    public static int hexRowOffset(int s, int i) {
        int I = i;
        if (i >= s) {
            I = 2 * s - 1 - I;
        }
        return -I;
    }

    public static void addRow(TETile[][] world, Position p, int width, TETile t) {
        for (int xi = 0; xi < width; xi += 1) {
            int xCoord = p.x + xi;
            int yCoord = p.y;
            world[xCoord][yCoord] = TETile.colorVariant(t, 32, 32, 32, RANDOM);
        }
    }

    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        if (s < 2) {
            throw new IllegalArgumentException();
        }
        for (int yi = 0; yi < 2 * s; yi += 1) {
            int thisRowY = p.y + yi;
            int xRowStart = p.x + hexRowOffset(s, yi);
            Position rowStartP = new Position(xRowStart, thisRowY);
            int rowWidth = hexRowWidth(s, yi);
            addRow(world, rowStartP, rowWidth, t);
        }
    }

    public static void drawRandomVerticalHexes(TETile[][] world, Position p, int s, int N) {
        Position nextp = new Position(p.x, p.y);
        for (int i = 0; i < N; i += 1) {
            addHexagon(world, nextp, s, randomTile());
            nextp.y += 2 * s;
        }
    }

    public static void drawTesselationofHexagons(TETile[][] world, Position p, int s) {
        Position nextp = new Position(p.x, p.y);
        int xOffset = xOffset(s);
        for (int i = 0; i < 5; i += 1) {
            drawRandomVerticalHexes(world, nextp, s, hexagonNum(i));
            nextp.x += xOffset;
            nextp.y += yOffset(s, i + 1);
        }
    }

    private static int hexagonNum(int i) {
        int N = i + 3;
        if (i > 2) {
            N = 10 - N;
        }
        return N;
    }

    private static int xOffset(int s) {
        return 2 * s + hexRowOffset(s, 0) - 1;
    }

    private static int yOffset(int s, int i) {
        int yOff = s;
        if (i > 2) {
            return yOff;
        }
        return -yOff;
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        switch (tileNum) {
            case 0: return Tileset.FLOWER;
            case 1: return Tileset.GRASS;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.SAND;
            default: return Tileset.TREE;
        }
    }


}
