package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.tileaction.*;
import idi.edu.idatt.mappe.services.ColorService;
import idi.edu.idatt.mappe.utils.CoordinateConverter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * View component for displaying the game board.
 * Handles rendering the board with tiles and special connections.
 */
public class BoardView {
    private static final Logger logger = Logger.getLogger(BoardView.class.getName());

    private final Pane boardPane;
    private final ColorService colorService;
    private final Board board;

    private double boardWidth;
    private double boardHeight;

    private final Map<Integer, Rectangle> tileRectangles = new HashMap<>();
    private final Map<Integer, Integer> ladderDestinations = new HashMap<>();
    private final Map<Integer, Integer> snakeDestinations = new HashMap<>();

    /**
     * Creates a new BoardView.
     *
     * @param boardPane The pane to draw the board on
     * @param colorService The color service for tile colors
     * @param board The board to display
     * @param boardWidth The width of the board
     * @param boardHeight The height of the board
     */
    public BoardView(Pane boardPane, ColorService colorService, Board board,
                     double boardWidth, double boardHeight) {
        this.boardPane = boardPane;
        this.colorService = colorService;
        this.board = board;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        identifySpecialTileDestinations();
        drawBoard();

        logger.info("BoardView initialized");
    }

    /**
     * Identifies destinations of special tiles like ladders and snakes.
     */
    private void identifySpecialTileDestinations() {
        ladderDestinations.clear();
        snakeDestinations.clear();

        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;

            TileAction action = tile.getLandAction();
            if (action instanceof LadderTileAction ladderTileAction) {
                int targetId = ladderTileAction.getDestinationTileId();
                ladderDestinations.put(targetId, tile.getIndex());
            } else if (action instanceof SnakeTileAction snakeTileAction) {
                int targetId = snakeTileAction.getDestinationTileId();
                snakeDestinations.put(targetId, tile.getIndex());
            }
        }
    }
    /**
     * Draws the game board with all tiles and special connections.
     */
    private void drawBoard() {
        if (board == null || board.getTiles() == null || board.getTiles().isEmpty()) {
            logger.severe("Board or tiles are not initialized or empty.");
            return;
        }

        // Clear existing board elements
        boardPane.getChildren().clear();
        tileRectangles.clear();

        int rows = board.getRows() > 0 ? board.getRows() : 9;
        int cols = board.getColumns() > 0 ? board.getColumns() : 10;

        double tileWidth = boardWidth / cols;
        double tileHeight = boardHeight / rows;

        List<ConnectionView> connections = new ArrayList<>();

        GameType gameType = board.getGameType();

        // First pass: create all the tiles with appropriate colors
        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;

            int x = tile.getX();
            int y = tile.getY();
            int tileIndex = tile.getIndex();

            double[] screenPos = CoordinateConverter.boardToScreen(
                    y, x, rows, cols, boardWidth, boardHeight);

            Color tileColor;
            TileAction action = tile.getLandAction();

            if (GameType.LUDO.equals(gameType)) {
                if (tile.isSafeZone()) {
                    tileColor = colorService.getSafeZoneColor();
                } else if (action instanceof StartingAreaTileAction) {
                    tileColor = colorService.getStartingAreaColor();
                } else if (action instanceof WinTileAction) {
                    tileColor = colorService.getSafeZoneColor();
                } else if (action instanceof CaptureTileAction) {
                    tileColor = colorService.getTileColor(action);
                } else {
                    tileColor = colorService.getHomeColor();
                }
            } else {
                if (ladderDestinations.containsKey(tileIndex)) {
                    tileColor = colorService.getLadderDestinationColor();
                } else if (snakeDestinations.containsKey(tileIndex)) {
                    tileColor = colorService.getSnakeDestinationColor();
                } else {
                    tileColor = colorService.getTileColor(action);
                }
            }

            Rectangle rect = createTileRectangle(screenPos[0], screenPos[1], tileWidth, tileHeight, tileColor);

            if (tile.isSafeZone()) {
                rect.getStyleClass().add("tile-safe-zone");
            } else if (action instanceof StartingAreaTileAction) {
                rect.getStyleClass().add("tile-starting-area");
            } else if (action instanceof WinTileAction) {
                rect.getStyleClass().add("tile-win");
            } else if (action instanceof CaptureTileAction) {
                rect.getStyleClass().add("tile-capture");
            } else if (action instanceof LadderTileAction) {
                rect.getStyleClass().add("tile-ladder-start");
            } else if (action instanceof SnakeTileAction) {
                rect.getStyleClass().add("tile-snake-start");
            } else if (action != null) {
                rect.getStyleClass().add("tile-special");
            } else {
                rect.getStyleClass().add("tile-regular");
            }

            tileRectangles.put(tileIndex, rect);

            // Add tile number
            Text tileIdText = new Text(screenPos[0] + 10, screenPos[1] + 20, String.valueOf(tileIndex));
            tileIdText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            boardPane.getChildren().addAll(tileRectangles.get(tileIndex), tileIdText);
        }

        // Second pass: create connections (for Snakes and Ladders)
        if (!GameType.LUDO.equals(gameType)) {
            for (Tile tile : board.getTiles().values()) {
                if (tile == null) continue;

                TileAction action = tile.getLandAction();
                if (action instanceof LadderTileAction ladderTileAction) {
                    int targetId = ladderTileAction.getDestinationTileId();
                    Tile targetTile = board.getTileByIndex(targetId);
                    if (targetTile != null) {
                        ConnectionView ladder = new ConnectionView(
                                tile,
                                targetTile,
                                ConnectionView.Type.LADDER,
                                rows,
                                cols,
                                boardWidth,
                                boardHeight
                        );
                        connections.add(ladder);
                    }
                } else if (action instanceof SnakeTileAction snakeTileAction) {
                    int targetId = snakeTileAction.getDestinationTileId();
                    Tile targetTile = board.getTileByIndex(targetId);
                    if (targetTile != null) {
                        ConnectionView snake = new ConnectionView(
                                tile,
                                targetTile,
                                ConnectionView.Type.SNAKE,
                                rows,
                                cols,
                                boardWidth,
                                boardHeight
                        );
                        connections.add(snake);
                    }
                }
            }

            boardPane.getChildren().addAll(connections);
        }

        logger.info("Board drawn with " + board.getTiles().size() + " tiles");
    }

    /**
     * Draws a home base for a Ludo player
     */
    private void drawHomeBase(double x, double y, double width, double height, Color color) {
        Rectangle homeBase = new Rectangle(x, y, width, height);
        homeBase.setFill(color);
        homeBase.setStroke(Color.BLACK);
        homeBase.setStrokeWidth(1.5);

        double innerX = x + width * 0.25;
        double innerY = y + height * 0.25;
        double innerWidth = width * 0.5;
        double innerHeight = height * 0.5;

        Rectangle innerSquare = new Rectangle(innerX, innerY, innerWidth, innerHeight);
        innerSquare.setFill(Color.WHITE);
        innerSquare.setStroke(Color.BLACK);
        innerSquare.setStrokeWidth(1.0);

        boardPane.getChildren().addAll(homeBase, innerSquare);

        double startPosWidth = width * 0.15;
        double startPosHeight = height * 0.15;

        Rectangle pos1 = new Rectangle(innerX + innerWidth * 0.2, innerY + innerHeight * 0.2,
                startPosWidth, startPosHeight);
        pos1.setFill(color);
        pos1.setStroke(Color.BLACK);
        pos1.setStrokeWidth(0.5);

        Rectangle pos2 = new Rectangle(innerX + innerWidth * 0.65, innerY + innerHeight * 0.2,
                startPosWidth, startPosHeight);
        pos2.setFill(color);
        pos2.setStroke(Color.BLACK);
        pos2.setStrokeWidth(0.5);

        Rectangle pos3 = new Rectangle(innerX + innerWidth * 0.2, innerY + innerHeight * 0.65,
                startPosWidth, startPosHeight);
        pos3.setFill(color);
        pos3.setStroke(Color.BLACK);
        pos3.setStrokeWidth(0.5);

        Rectangle pos4 = new Rectangle(innerX + innerWidth * 0.65, innerY + innerHeight * 0.65,
                startPosWidth, startPosHeight);
        pos4.setFill(color);
        pos4.setStroke(Color.BLACK);
        pos4.setStrokeWidth(0.5);

        boardPane.getChildren().addAll(pos1, pos2, pos3, pos4);
    }

    /**
     * Draws a home path for a Ludo player (the colored path to the center)
     */
    private void drawHomePath(int startRow, int startCol, int rowLength, int colLength, Color color) {
        int rows = board.getRows() > 0 ? board.getRows() : 15;
        int cols = board.getColumns() > 0 ? board.getColumns() : 15;

        double tileWidth = boardWidth / cols;
        double tileHeight = boardHeight / rows;

        for (int row = startRow; row < startRow + rowLength; row++) {
            for (int col = startCol; col < startCol + colLength; col++) {
                double[] screenPos = CoordinateConverter.boardToScreen(
                        col, row, rows, cols, boardWidth, boardHeight);

                Rectangle pathTile = new Rectangle(screenPos[0], screenPos[1], tileWidth, tileHeight);
                pathTile.setFill(color.deriveColor(0, 1, 1.2, 0.7)); // Lighter version of the color
                pathTile.setStroke(Color.BLACK);
                pathTile.setStrokeWidth(1.0);

                boardPane.getChildren().add(pathTile);
            }
        }
    }

    /**
     * Creates a rectangle for a tile with the specified properties.
     *
     * @param x The x-coordinate of the tile
     * @param y The y-coordinate of the tile
     * @param width The width of the tile
     * @param height The height of the tile
     * @param color The color of the tile
     * @return A Rectangle representing the tile
     */
    private Rectangle createTileRectangle(double x, double y, double width, double height, Color color) {
        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setFill(color);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(1.5);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.getStyleClass().add("tile");
        return rect;
    }


    /**
     * Gets the board being displayed.
     *
     * @return The board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the rectangle for a specific tile.
     *
     * @param tileIndex The index of the tile
     * @return The rectangle, or null if not found
     */
    public Rectangle getTileRectangle(int tileIndex) {
        return tileRectangles.get(tileIndex);
    }

    /**
     * Gets the board width.
     *
     * @return The board width
     */
    public double getBoardWidth() {
        return boardWidth;
    }

    /**
     * Gets the board height.
     *
     * @return The board height
     */
    public double getBoardHeight() {
        return boardHeight;
    }

    /**
     * Calculates the tile dimensions.
     *
     * @return An array with [width, height] of tiles
     */
    public double[] getTileDimensions() {
        int rows = board.getRows() > 0 ? board.getRows() : 9;
        int cols = board.getColumns() > 0 ? board.getColumns() : 10;

        return new double[] {boardWidth / cols, boardHeight / rows};
    }

    /**
     * Calculates the screen position of a tile.
     *
     * @param tile The tile
     * @return An array with [x, y] screen coordinates
     */
    public double[] getTilePosition(Tile tile) {
        if (tile == null) return null;

        int rows = board.getRows() > 0 ? board.getRows() : 9;
        int cols = board.getColumns() > 0 ? board.getColumns() : 10;

        return CoordinateConverter.boardToScreen(
                tile.getY(), tile.getX(), rows, cols, boardWidth, boardHeight);
    }
}
