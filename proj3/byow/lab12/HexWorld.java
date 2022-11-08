package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);


    /**
     * Draws a hexagon with side length s with the top left corner at coordinate x, y
     */
    public static void addHexagon(TETile[][] tiles, int s, int x, int y, TETile type) {
        // draw top half
        for (int i = 0; i < s; i++) {
            fillRow(tiles, s + i*2, x - i, y - i, type);
        }

        // starting width and x, y coordinates for bottom half
        int width = s + 2*(s-1);
        int bottomX = x - (s-1);
        int bottomY = y - s;

        // draw bottom half
        for (int i = 0; i < s; i++) {
            fillRow(tiles,  width - i*2, bottomX + i, bottomY - i, type);
        }

    }

    /**
     * Draws a row of characters of given width and x,y coordinates
     */
    public static void fillRow(TETile[][] tiles, int width, int x, int y, TETile type) {
        for (int i = 0; i < width; i++) {
            tiles[x+i][y] = type;
        }
    }

    /**
     * Fills the given 2D array of tiles with empty tiles.
     * @param tiles
     */
    public static void fillEmptyTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Fills the given 2D array of tiles with a tesselation of size tessSize composed of hexagons of size s at position x, y
     */
    public static void terrainGenerator(TETile[][] tiles, int tessSize, int hexSize, int x, int y) {
        int numHexagons = 2*tessSize - 1;
        int xOffset = 2*hexSize - 1;

        // Central column
        fillTerrainColumn(tiles, x, y, hexSize, numHexagons);

        // Outward columns
        for (int i = 1; i < tessSize; i++) {
            fillTerrainColumn(tiles, x - i*xOffset, y - i*hexSize, hexSize, numHexagons - i);
            fillTerrainColumn(tiles, x + i*xOffset, y - i*hexSize, hexSize, numHexagons - i);
        }

    }

    public static void fillTerrainColumn(TETile[][] tiles, int startX, int startY, int s, int length) {
        for (int i = 0; i < length; i++) {
            addHexagon(tiles, s, startX, startY - i*2*s, randomTile());
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.MOUNTAIN;
            case 2: return Tileset.SAND;
            case 3: return Tileset.TREE;
            case 4: return Tileset.FLOWER;
            default: return Tileset.NOTHING;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];

        // initialize the tiles
        fillEmptyTiles(tiles);

        // create the terrain
        terrainGenerator(tiles, 4, 4, 50, 80);

        // render the tiles
        ter.renderFrame(tiles);
    }
}
