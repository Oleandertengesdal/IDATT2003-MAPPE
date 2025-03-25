package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.utils.CoordinateConverter;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;

/**
 * A JavaFX component that displays a game board
 */
public class BoardGameView extends Pane {
    private Board board;
    private Canvas boardCanvas;
    private Map<Player, PlayerToken> playerTokens;
    private double canvasWidth;
    private double canvasHeight;

    /**
     * Creates a new game board view
     *
     * @param board The board to display
     * @param width The width of the view
     * @param height The height of the view
     */
    public BoardGameView(Board board, double width, double height) {
        this.board = board;
        this.playerTokens = new HashMap<>();
        this.canvasWidth = width;
        this.canvasHeight = height;

        boardCanvas = new Canvas(width, height);
        getChildren().add(boardCanvas);

        drawBoard();
    }

    /**
     * Draws the game board
     */
    private void drawBoard() {
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());

        for (Tile tile : board.getTiles().values()) {
            drawTile(gc, tile);
        }
    }

    /**
     * Draws a single tile
     *
     * @param gc The graphics context
     * @param tile The tile to draw
     */
    private void drawTile(GraphicsContext gc, Tile tile) {
        double[] screenPos = CoordinateConverter.boardToScreen(
                tile.getY(),  // r (row)
                tile.getX(),  // c (column)
                board.getRows() - 1,  // rmax
                board.getColumns() - 1,  // cmax
                canvasWidth,  // xmax
                canvasHeight  // ymax
        );

        double x = screenPos[0];
        double y = screenPos[1];

        double tileWidth = canvasWidth / board.getColumns();
        double tileHeight = canvasHeight / board.getRows();

        gc.setFill(Color.WHITE);
        gc.fillRect(x + 2, y + 2, tileWidth - 4, tileHeight - 4);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(x + 2, y + 2, tileWidth - 4, tileHeight - 4);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font(14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(tile.getIndex()),
                x + tileWidth / 2,
                y + tileHeight / 2 + 5);
    }

    /**
     * Adds a player to the board
     *
     * @param player The player to add
     * @param color The color of the player's token
     */
    public void addPlayer(Player player, Color color) {
        PlayerToken token = new PlayerToken(player, color);
        playerTokens.put(player, token);
        getChildren().add(token);
        updatePlayerPosition(player);
    }

    /**
     * Updates the position of a player's token
     *
     * @param player The player whose position to update
     */
    public void updatePlayerPosition(Player player) {
        PlayerToken token = playerTokens.get(player);
        if (token != null) {
            Tile currentTile = board.getTileByIndex(player.getCurrentTileIndex());
            if (currentTile != null) {
                double[] screenPos = CoordinateConverter.boardToScreen(
                        currentTile.getY(),  // r (row)
                        currentTile.getX(),  // c (column)
                        board.getRows() - 1,  // rmax
                        board.getColumns() - 1,  // cmax
                        canvasWidth,  // xmax
                        canvasHeight  // ymax
                );

                double tileWidth = canvasWidth / board.getColumns();
                double tileHeight = canvasHeight / board.getRows();

                // Position the token in the center of the tile
                token.setTranslateX(screenPos[0] + tileWidth / 2 - token.getRadius());
                token.setTranslateY(screenPos[1] + tileHeight / 2 - token.getRadius());
            }
        }
    }

    /**
     * Updates the board view
     */
    public void update() {
        drawBoard();
        playerTokens.keySet().forEach(this::updatePlayerPosition);
    }

    /**
     * A visual representation of a player on the board
     */
    private class PlayerToken extends javafx.scene.shape.Circle {
        private Player player;

        /**
         * Creates a new player token
         *
         * @param player The player
         * @param color The color of the token
         */
        public PlayerToken(Player player, Color color) {
            super(15, color);
            this.player = player;
            setStroke(Color.BLACK);
            setStrokeWidth(2);
        }
    }
}