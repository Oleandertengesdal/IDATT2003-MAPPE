package idi.edu.idatt.mappe.views.game;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.services.AnimationController;
import idi.edu.idatt.mappe.services.TokenService;
import idi.edu.idatt.mappe.utils.CoordinateConverter;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.logging.Logger;

/**
 * View component for managing player tokens on the board.
 * Handles rendering and animating player tokens.
 */
public class PlayerTokenView {
    private static final Logger logger = Logger.getLogger(PlayerTokenView.class.getName());

    private final Pane boardPane;
    private final TokenService tokenService;
    private final AnimationController animationController;
    private final Map<Player, Node> playerTokens = new HashMap<>();
    private final Map<Player, Color> playerColors = new HashMap<>();

    private final Map<Integer, List<Player>> tileOccupancy = new HashMap<>();

    private double boardWidth;
    private double boardHeight;

    private final double PLAYER_OFFSET_HORIZONTAL = 15.0;
    private final double PLAYER_OFFSET_VERTICAL = 15.0;

    private Board board;
    private BoardView boardView;

    /**
     * Creates a new PlayerTokenView.
     *
     * @param boardPane The pane to add tokens to
     * @param tokenService The token service for creating tokens
     * @param animationController The animation service for animating tokens
     * @param boardWidth The width of the board
     * @param boardHeight The height of the board
     */
    public PlayerTokenView(Pane boardPane, TokenService tokenService,
                           AnimationController animationController,
                           double boardWidth, double boardHeight) {
        this.boardPane = boardPane;
        this.tokenService = tokenService;
        this.animationController = animationController;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        logger.info("PlayerTokenView initialized");
    }

    /**
     * Sets the current board for this view.
     *
     * @param board The board to use
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Sets the BoardView to access tile rectangles.
     *
     * @param boardView The BoardView
     */
    public void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }

    /**
     * Adds a player token to the board.
     *
     * @param player The player to add
     * @param color The color for the player's token
     */
    public void addPlayerToken(Player player, Color color) {
        if (playerTokens.containsKey(player)) {
            logger.warning("Player " + player.getName() + " already has a token");
            return;
        }

        Node tokenNode = tokenService.createTokenNode(player, color);
        playerTokens.put(player, tokenNode);
        playerColors.put(player, color);

        boardPane.getChildren().add(tokenNode);

        // Place player on first tile (tile with index 1)
        if (board != null) {
            Tile firstTile = board.getTileByIndex(1);
            if (firstTile != null) {
                player.placeOnTile(firstTile);
            } else {
                logger.warning("Could not find first tile");
            }
        }

        updatePlayerPosition(player);

        logger.info("Added token for player: " + player.getName() + " with color: " + color);
    }

    /**
     * Updates a player's position on the board.
     *
     * @param player The player to update
     */
    public void updatePlayerPosition(Player player) {
        if (player == null || player.getCurrentTile() == null) {
            logger.warning("Cannot update position: player or current tile is null");
            return;
        }

        Node token = playerTokens.get(player);
        if (token == null) {
            logger.warning("No token found for player: " + player.getName());
            return;
        }

        Tile currentTile = player.getCurrentTile();
        int tileIndex = currentTile.getIndex();

        updateTileOccupancy(player, tileIndex);

        if (boardView != null) {
            Rectangle tileRect = boardView.getTileRectangle(tileIndex);
            if (tileRect != null) {
                positionPlayerOnTileRectangle(player, token, tileRect);
                return;
            }
        }

        positionPlayerUsingCoordinates(player, token, currentTile);

        logger.info("Updated position for player: " + player.getName() +
                " to tile: " + tileIndex);
    }

    /**
     * Positions a player token on a tile rectangle.
     *
     * @param player The player to position
     * @param token The player's token node
     * @param tileRect The rectangle representing the tile
     */
    private void positionPlayerOnTileRectangle(Player player, Node token, Rectangle tileRect) {
        if (player == null || token == null || tileRect == null) {
            return;
        }

        int tileIndex = player.getCurrentTile().getIndex();

        double tileCenterX = tileRect.getX() + tileRect.getWidth() / 2;
        double tileCenterY = tileRect.getY() + tileRect.getHeight() / 2;

        getPlayerOffset(player, token, tileIndex, tileCenterX, tileCenterY);
    }


    /**
     * Positions a player token using coordinate-based calculation.
     * This is a fallback method when tile rectangles aren't available.
     *
     * @param player The player to position
     * @param token The player's token node
     * @param tile The tile the player is on
     */
    private void positionPlayerUsingCoordinates(Player player, Node token, Tile tile) {
        if (player == null || token == null || tile == null) {
            return;
        }

        int tileIndex = tile.getIndex();
        int rows = board.getRows();
        int cols = board.getColumns();

        double[] screenPos = CoordinateConverter.boardToScreen(
                tile.getY(), tile.getX(), rows, cols, boardWidth, boardHeight);

        double tileWidth = boardWidth / cols;
        double tileHeight = boardHeight / rows;

        double tileCenterX = screenPos[0] + tileWidth / 2;
        double tileCenterY = screenPos[1] + tileHeight / 2;

        getPlayerOffset(player, token, tileIndex, tileCenterX, tileCenterY);


    }

    private void getPlayerOffset(Player player, Node token, int tileIndex, double tileCenterX, double tileCenterY) {
        double[] offset = calculatePlayerOffset(tileIndex, player);

        double tokenWidth = token.getBoundsInLocal().getWidth();
        double tokenHeight = token.getBoundsInLocal().getHeight();

        double finalX = tileCenterX - tokenWidth / 2 + offset[0];
        double finalY = tileCenterY - tokenHeight / 2 + offset[1];

        animationController.setTokenPosition(token, finalX, finalY);
    }

    /**
     * Updates the tile occupancy tracking when a player moves.
     *
     * @param player The player that moved
     * @param tileIndex The index of the tile the player moved to
     */
    private void updateTileOccupancy(Player player, int tileIndex) {
        for (List<Player> players : tileOccupancy.values()) {
            players.remove(player);
        }

        tileOccupancy.computeIfAbsent(tileIndex, k -> new ArrayList<>()).add(player);
    }

    /**
     * Calculates the offset for a player token within a tile based on how many
     * players are on the same tile.
     *
     * @param tileIndex The index of the tile
     * @param player The player to calculate offset for
     * @return An array with [x offset, y offset]
     */
    private double[] calculatePlayerOffset(int tileIndex, Player player) {
        List<Player> playersOnTile = tileOccupancy.getOrDefault(tileIndex, Collections.emptyList());
        int playerPosition = playersOnTile.indexOf(player);

        if (playerPosition == -1 || playersOnTile.size() <= 1) {
            return new double[]{0, 0};
        }

        if (playersOnTile.size() == 2) {
            return new double[]{
                    playerPosition == 0 ? -PLAYER_OFFSET_HORIZONTAL/2 : PLAYER_OFFSET_HORIZONTAL/2,
                    0
            };
        }

        if (playersOnTile.size() <= 4) {
            int row = playerPosition / 2;
            int col = playerPosition % 2;
            return new double[]{
                    col == 0 ? -PLAYER_OFFSET_HORIZONTAL/2 : PLAYER_OFFSET_HORIZONTAL/2,
                    row == 0 ? -PLAYER_OFFSET_VERTICAL/2 : PLAYER_OFFSET_VERTICAL/2
            };
        }

        int maxPlayersPerRow = (int)Math.ceil(Math.sqrt(playersOnTile.size()));
        int row = playerPosition / maxPlayersPerRow;
        int col = playerPosition % maxPlayersPerRow;

        double rowOffset = (maxPlayersPerRow - 1) / 2.0;
        double colOffset = (playersOnTile.size() / maxPlayersPerRow) / 2.0;

        return new double[]{
                (col - rowOffset) * PLAYER_OFFSET_HORIZONTAL,
                (row - colOffset) * PLAYER_OFFSET_VERTICAL
        };
    }
}