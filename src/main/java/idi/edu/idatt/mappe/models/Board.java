package idi.edu.idatt.mappe.models;

import java.util.HashMap;
import java.util.Map;

import static idi.edu.idatt.mappe.validators.BoardValidator.boardSizeValidator;
import static idi.edu.idatt.mappe.validators.TileValidator.validateTileIndex;

/**
 * The Board class represents the game board of the game.
 */
public class Board {
    Map<Integer, Tile> tiles;

    /**
     * Creates a new board with the given tiles
     *
     * @param tiles The tiles of the board
     */
    public Board(Map<Integer, Tile> tiles) {
        this.tiles = tiles;
    }

    /**
     * Creates a new board with no tiles
     */
    public Board() {
       tiles = new HashMap<>();
    }

    /**
     * Creates a new board with the given size
     *
     * @param size The size of the board
     */
    public Board(int size) {
        boardSizeValidator(size);
        tiles = new HashMap<>();
        for (int i = 1; i <= size; i++) {
            addTile(i, new Tile(i));
        }
        // Connect the tiles
        for (int i = 1; i <= size - 1; i++) {
            tiles.get(i).setNextTile(tiles.get(i + 1));
        }
    }

    /**
     * Adds a tile to the board
     *
     * @param tile The tile to add
     */
    public void addTile(int index, Tile tile) {
        tiles.put(index, tile);
    }

    /**
     * Returns the tile at the given index
     *
     * @param tileIndex The index of the tile
     * @return The tile at the given index
     */
    public Tile getTile(int tileIndex) {
        validateTileIndex(tileIndex, tiles.size());
        return tiles.get(tileIndex); // The index is 1-based
    }


    /**
     * Sets the tile at the given index
     *
     * @param index The index of the tile
     * @param tile The tile to set
     */
    public void setTile(int index, Tile tile) {
        validateTileIndex(index, tiles.size());
        tiles.put(index, tile);
    }

    /**
     * Returns the tiles of the board
     *
     * @return The tiles of the board
     */
    public Map<Integer, Tile> getTiles() {
        return tiles;
    }
}
