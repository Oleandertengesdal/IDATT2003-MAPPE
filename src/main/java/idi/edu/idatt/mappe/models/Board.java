package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.GameType;


import java.util.*;
import java.util.logging.Logger;

import static idi.edu.idatt.mappe.validators.BoardValidator.boardSizeValidator;
import static idi.edu.idatt.mappe.validators.TileValidator.validateTileIndex;

/**
 * The Board class represents the game board of the game.
 */
public class Board {
    private Map<Integer, Tile> tiles;
    private int rows;
    private int columns;
    private GameType gameType;

    private static final Logger logger = Logger.getLogger(Board.class.getName());

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
     *
     * @param gameType The type of game this board is for
     */
    public Board(GameType gameType) {
        this.gameType = gameType;
        tiles = new HashMap<>();
    }

    /**
     * Creates a new board with the given size
     *
     * @param size The size of the board
     * @param gameType The type of game this board is for
     */
    public Board(int size, GameType gameType) {
        boardSizeValidator(size);
        this.gameType = gameType;

        tiles = new HashMap<>();

        switch (gameType) {
            case GameType.SNAKES_AND_LADDERS -> setupSnakesAndLaddersBoard();
            case GameType.THE_LOST_DIAMOND -> logger.info("Creating a board for The Lost Diamond game");
            default -> setupSnakesAndLaddersBoard();

        }
    }

    /**
     * Creates a new board with the given number of rows and columns
     *
     * @param rows The number of rows
     * @param columns The number of columns
     * @param gameType The type of game this board is for
     */
    public Board(int rows, int columns, GameType gameType) {
        boardSizeValidator(rows * columns);
        this.rows = rows;
        this.columns = columns;
        this.gameType = gameType;

        tiles = new HashMap<>();

        switch (gameType) {
            case GameType.SNAKES_AND_LADDERS -> setupSnakesAndLaddersBoard();
            case GameType.THE_LOST_DIAMOND -> logger.info("Creating a board for The Lost Diamond game");
            default -> setupSnakesAndLaddersBoard();
        }
        }


    /**
     * Sets up a Snakes and Ladders board with the snake pattern
     */
    private void setupSnakesAndLaddersBoard() {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int tileId = calculateSnakePatternTileId(i, j);
                Tile tile = new Tile(tileId, j, i);
                addTile(tileId, tile);
            }
        }

        for (int i = 1; i < rows * columns; i++) {
            tiles.get(i).setNextTile(tiles.get(i + 1));
        }
    }

    /**
     * Calculates the tile ID based on row and column for the snake pattern
     *
     * @param row The row index (0-based)
     * @param col The column index (0-based)
     * @return The tile ID
     */
    private int calculateSnakePatternTileId(int row, int col) {
        if (row % 2 == 0) {
            return row * columns + col + 1;
        } else {
            return (row + 1) * columns - col;
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
        try {
            validateTileIndex(tileIndex, tiles.size());
            return tiles.get(tileIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Tile with index " + tileIndex + " not found.");
        }
    }

    /**
     * Returns the tile at the given coordinates
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return The tile at the given coordinates, or null if no tile exists
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
     * Returns the tiles of the board
     *
     * @return The tiles of the board
     */
    public Map<Integer, Tile> getTiles() {
        return new HashMap<>(tiles);
    }

    /**
     * Returns the game type of this board
     *
     * @return The game type
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * Sets the game type of this board
     *
     * @param gameType The game type to set
     */
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    /**
     * Gets the rows in the board
     *
     * @return The number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets the columns in the board
     *
     * @return The number of columns
     */
    public int getColumns() {
        return columns;
    }
}