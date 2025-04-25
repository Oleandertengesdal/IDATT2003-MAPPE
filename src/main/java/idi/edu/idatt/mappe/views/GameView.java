package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.models.*;
import idi.edu.idatt.mappe.models.tileaction.*;
import idi.edu.idatt.mappe.utils.CoordinateConverter;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameView extends Pane implements BoardGameObserver {

    private final double canvasWidth = 800;
    private final double canvasHeight = 800;

    private final int rows = 9;
    private final int cols = 10;

    private final Board board;
    private final Map<Player, Circle> playerTokens = new HashMap<>();

    // Maps to track destination tiles of ladders and snakes
    private final Map<Integer, Integer> ladderDestinations = new HashMap<>();
    private final Map<Integer, Integer> snakeDestinations = new HashMap<>();

    private final static Logger logger = Logger.getLogger(GameView.class.getName());

    public GameView(Board board) {
        this.board = board;
        setPrefSize(canvasWidth, canvasHeight);

        // First, identify all ladder and snake destinations
        identifySpecialTileDestinations();

        // Then draw the board
        drawBoard();
    }

    private void identifySpecialTileDestinations() {
        // Scan the board to identify ladder and snake destinations
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

    private void drawBoard() {
        if (board == null || board.getTiles() == null || board.getTiles().isEmpty()) {
            logger.severe("Board or tiles are not initialized or empty.");
            return;
        }

        double tileWidth = canvasWidth / cols;
        double tileHeight = canvasHeight / rows;

        List<ConnectionView> connections = new ArrayList<>();

        // First pass: create all the tiles with appropriate colors
        for (Tile tile : board.getTiles().values()) {
            if (tile == null) continue;

            int x = tile.getX();
            int y = tile.getY();
            int tileIndex = tile.getIndex();

            double[] screenPos = CoordinateConverter.boardToScreen(y, x, rows, cols, canvasWidth, canvasHeight);

            // Determine the color based on tile type, including special destinations
            Color tileColor;

            if (ladderDestinations.containsKey(tileIndex)) {
                // This is a ladder destination
                tileColor = Color.web("#90EE90"); //Green
            } else if (snakeDestinations.containsKey(tileIndex)) {
                // This is a snake destination
                tileColor = Color.web("#FFC0CB"); //Red
            } else {
                // Regular tile or tile with action
                TileAction action = tile.getLandAction();
                tileColor = switch (action) {
                    case LadderTileAction ladderTileAction -> Color.web("#d0f0c0"); // Light green
                    case SnakeTileAction snakeTileAction -> Color.web("#ffd1d1"); // Light red
                    case RandomTeleportTileAction randomTeleportTileAction -> Color.LIGHTBLUE;
                    case SwapTileAction swapTileAction -> Color.GOLD;
                    case null, default -> Color.BEIGE;
                };
            }

            Rectangle rect = new Rectangle(screenPos[0], screenPos[1], tileWidth, tileHeight);
            rect.setFill(tileColor);
            rect.setStroke(Color.BLACK);
            rect.setArcWidth(15);
            rect.setArcHeight(15);

            Text tileIdText = new Text(screenPos[0] + 5, screenPos[1] + 15, String.valueOf(tile.getIndex()));
            tileIdText.setStyle("-fx-font-size: 12;");

            getChildren().addAll(rect, tileIdText);
        }

        // Second pass: create connections (snakes and ladders)
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
                            canvasWidth,
                            canvasHeight
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
                            canvasWidth,
                            canvasHeight
                    );
                    connections.add(snake);
                }
            }
        }

        // Add all connections on top of the tiles
        getChildren().addAll(connections);
    }

    /**
     * Adds a players token to the board.
     * Each player is represented by a colored circle.
     * The color of the player is randomly selected.
     *
     * @param player The player to add.
     */
    public void addPlayer(Player player) {
        Circle token = new Circle(10);
        token.setFill(Color.color(Math.random(), Math.random(), Math.random()));
        playerTokens.put(player, token);
        getChildren().add(token);
        updatePlayerPosition(player);
    }

    /**
     * Updates the position of the player token on the board.
     * This method is called whenever a player moves.
     * The function takes to account the possibility of mulitple players on the same tile,
     * when mulitple players are on the same tile they will be offset from each other to
     * make it easier to see all players.
     *
     * @param player The players whose position to update.
     */
    private void updatePlayerPosition(Player player) {
        // Group players by their current tile
        Map<Tile, List<Player>> playersOnTiles = playerTokens.keySet().stream()
                .filter(p -> p.getCurrentTile() != null)
                .collect(Collectors.groupingBy(Player::getCurrentTile));

        // Update the position of the player's token
        Tile tile = player.getCurrentTile();
        if (tile == null) return;

        double[] pos = CoordinateConverter.boardToScreen(tile.getY(), tile.getX(), rows, cols, canvasWidth, canvasHeight);
        List<Player> playersOnTile = playersOnTiles.get(tile);

        if (playersOnTile != null) {
            int index = playersOnTile.indexOf(player);
            // Calculate offsets for multiple players on the same tile.
            // This makes it possible to idetify different plauers on the same tile.
            double offsetX = (index % 2 == 0 ? -1 : 1) * ((double) index / 2) * 10;
            double offsetY = (index % 2 == 0 ? -1 : 1) * ((double) index / 2) * 10;

            Circle token = playerTokens.get(player);
            token.setLayoutX(pos[0] + 20 + offsetX);
            token.setLayoutY(pos[1] + 20 + offsetY);
        }
    }

    @Override
    public void onPlayerMoved(Player player, int steps) {
        updatePlayerPosition(player);
    }

    @Override
    public void onGameStateChanged(GameState gameState) {
        // Update the window in the UI.
        // This is not implemented yet.
    }

    @Override
    public void onGameWinner(Player winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Winner!");
        alert.setContentText(winner.getName() + " has won the game!");
        alert.showAndWait();
    }
}