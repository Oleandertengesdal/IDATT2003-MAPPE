package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.exceptions.TileNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static idi.edu.idatt.mappe.validators.BoardValidator.boardSizeValidator;
import static idi.edu.idatt.mappe.validators.TileValidator.validateTileIndex;

/**
 * The Board class represents the game board of the game.
 */
public class Board {
    private Map<Integer, Tile> tiles;
    private int rows;
    private int columns;

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
     * Creates a new board with the given number of rows and columns
     *
     * @param rows The number of rows
     * @param columns The number of columns
     */
    public Board(int rows, int columns) {
        boardSizeValidator(rows * columns);
        this.rows = rows;
        this.columns = columns;
        tiles = new HashMap<>();

        // Create tiles with coordinates
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Calculate tile ID based on row and column
                int tileId = calculateTileId(i, j);
                Tile tile = new Tile(tileId, j, i); // x=column, y=row
                addTile(tileId, tile);
            }
        }

        // Connect the tiles (based on the snake-like pattern as shown in the figure)
        setupTileConnections();
    }

    /**
     * Calculates the tile ID based on row and column
     * Uses a snake-like pattern as shown in the figure
     *
     * @param row The row index (0-based)
     * @param col The column index (0-based)
     * @return The tile ID
     */
    private int calculateTileId(int row, int col) {
        // Even rows go left to right, odd rows go right to left
        if (row % 2 == 0) {
            return row * columns + col + 1;
        } else {
            return (row + 1) * columns - col;
        }
    }

    /**
     * Sets up the connections between tiles based on the snake-like pattern
     */
    private void setupTileConnections() {
        for (int i = 1; i < rows * columns; i++) {
            tiles.get(i).setNextTile(tiles.get(i + 1));
        }
    }

    /**
     * Adds a tile to the board
     *
     * @param index The index of the tile
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
    public Tile getTileByIndex(int tileIndex) {
        validateTileIndex(tileIndex, tiles.size());
        return tiles.get(tileIndex);
    }

    /**
     * Returns the tile at the given coordinates
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The tile at the given coordinates, or null if no tile exists at the coordinates
     */
    public Tile getTileByCoordinates(int x, int y) {
        for (Tile tile : tiles.values()) {
            if (tile.getX() == x && tile.getY() == y) {
                return tile;
            }
        }
        return null;
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
        return new HashMap<>(tiles);
    }

    /**
     * Returns the number of rows in the board
     *
     * @return The number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the number of rows in the board
     *
     * @param rows The number of rows
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Returns the number of columns in the board
     *
     * @return The number of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the number of columns in the board
     *
     * @param columns The number of columns
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * Converts screen coordinates to board coordinates
     *
     * @param screenX The x-coordinate on the screen
     * @param screenY The y-coordinate on the screen
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     * @return An array containing the board coordinates [boardX, boardY]
     */
    public int[] screenToBoard(double screenX, double screenY, double screenWidth, double screenHeight) {
        int boardX = (int) (screenX / (screenWidth / columns));
        int boardY = (int) (screenY / (screenHeight / rows));
        return new int[]{boardX, boardY};
    }

    /**
     * Converts board coordinates to screen coordinates
     *
     * @param boardX The x-coordinate on the board
     * @param boardY The y-coordinate on the board
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     * @return An array containing the screen coordinates [screenX, screenY]
     */
    public double[] boardToScreen(int boardX, int boardY, double screenWidth, double screenHeight) {
        double tileWidth = screenWidth / columns;
        double tileHeight = screenHeight / rows;
        double screenX = boardX * tileWidth + (tileWidth / 2); // Center of the tile
        double screenY = boardY * tileHeight + (tileHeight / 2); // Center of the tile
        return new double[]{screenX, screenY};
    }

    /**
     * Method to get the row of the tile
     *
     * @param tileId The ID of the tile
     * @return The row of the tile
     */
    public int getRow(int tileId) {
        Tile tile = tiles.get(tileId);
        if (tile != null) {
            return tile.getX();
        }
        return -1;
    }

    /**
     * Method to get the column of the tile
     *
     * @param tileId The ID of the tile
     * @return The column of the tile
     */
    public int getCol(int tileId) {
        Tile tile = tiles.get(tileId);
        if (tile != null) {
            return tile.getY();
        }
        return -1;
    }
}