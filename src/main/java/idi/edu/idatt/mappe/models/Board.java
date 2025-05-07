package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private GameType gameType;

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

        if (gameType == GameType.LUDO) {
            setupLudoBoard((int) Math.round(Math.sqrt(size)), (int) Math.round(Math.sqrt(size)));
        } else {
            for (int i = 1; i <= size; i++) {
                addTile(i, new Tile(i));
            }
            for (int i = 1; i <= size - 1; i++) {
                tiles.get(i).setNextTile(tiles.get(i + 1));
            }
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

        if (gameType == GameType.LUDO) {
            setupLudoBoard(rows, columns);
        } else if (gameType == GameType.SNAKES_AND_LADDERS) {
            setupSnakesAndLaddersBoard();
        } else if (gameType == GameType.THE_LOST_DIAMOND) {
            setupLostDiamondBoard();
        } else {
            setupSnakesAndLaddersBoard();
        }
    }

    /**
     * Sets up a Snakes and Ladders board with the snake pattern
     */
    private void setupSnakesAndLaddersBoard() {
        tiles = new HashMap<>();

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
     * Sets up a Ludo board with a hollow square pattern
     *
     * @param rows The number of rows
     * @param columns The number of columns
     */
    private void setupLudoBoard(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        tiles = new HashMap<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int tileId = i * columns + j + 1;
                Tile tile = new Tile(tileId, j, i);

                boolean isPerimeterTile = (i == 0 || i == rows - 1 || j == 0 || j == columns - 1);
                if (!isPerimeterTile) {
                    tile.setDisabled(true);
                }

                addTile(tileId, tile);
            }
        }

        List<Integer> perimeterTileIds = getPerimeterTileIds();
        for (int i = 0; i < perimeterTileIds.size() - 1; i++) {
            tiles.get(perimeterTileIds.get(i)).setNextTile(tiles.get(perimeterTileIds.get(i + 1)));
        }

        if (!perimeterTileIds.isEmpty()) {
            tiles.get(perimeterTileIds.get(perimeterTileIds.size() - 1))
                    .setNextTile(tiles.get(perimeterTileIds.get(0)));
        }
    }

    /**
     * Sets up a board for The Lost Diamond game
     * This is just a placeholder - implement according to your specific requirements
     */
    private void setupLostDiamondBoard() {
        setupSnakesAndLaddersBoard();
    }

    /**
     * Gets a list of tile IDs that form the perimeter of the board
     * This is used for Ludo board setup
     *
     * @return List of tile IDs around the perimeter in clockwise order
     */
    public List<Integer> getPerimeterTileIds() {
        List<Integer> perimeter = new ArrayList<>();

        // Top row (left to right)
        for (int j = 0; j < columns; j++) {
            perimeter.add(j + 1);
        }

        // Right column (top to bottom, excluding the top corner already added)
        for (int i = 1; i < rows; i++) {
            perimeter.add(i * columns + columns);
        }

        // Bottom row (right to left, excluding the right corner already added)
        for (int j = columns - 2; j >= 0; j--) {
            perimeter.add((rows - 1) * columns + j + 1);
        }

        // Left column (bottom to top, excluding corners already added)
        for (int i = rows - 2; i > 0; i--) {
            perimeter.add(i * columns + 1);
        }

        return perimeter;
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
     * Gets the next tile on the perimeter (for Ludo movement)
     *
     * @param currentTileId The current tile ID
     * @param steps Number of steps to move
     * @return The ID of the destination tile
     */
    public int getNextPerimeterTileId(int currentTileId, int steps) {
        if (gameType != GameType.LUDO) {
            // For non-Ludo games, just add steps
            return Math.min(currentTileId + steps, tiles.size());
        }

        List<Integer> perimeter = getPerimeterTileIds();

        // Find the current position on the perimeter
        int currentPos = perimeter.indexOf(currentTileId);
        if (currentPos == -1) {
            // Not on perimeter, return starting position
            return perimeter.get(0);
        }

        // Calculate next position with wraparound
        int nextPos = (currentPos + steps) % perimeter.size();
        return perimeter.get(nextPos);
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

    /**
     * Sets up the connections between tiles for the standard snake pattern
     */
    private void setupTileConnections() {
        for (int i = 1; i < rows * columns; i++) {
            tiles.get(i).setNextTile(tiles.get(i + 1));
        }
    }
}