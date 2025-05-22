package idi.edu.idatt.mappe.services;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.tileaction.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Service for managing colors in the game.
 * Handles player colors, tile colors, and other color-related functionality.
 */
public class ColorService {
    private final Map<Player, Color> playerColors = new HashMap<>();
    private final Map<String, Color> actionColors = new HashMap<>();

    private static final Logger logger = Logger.getLogger(ColorService.class.getName());

    /**
     * Creates a new ColorService with default action colors.
     */
    public ColorService() {
        initializeActionColors();
    }

    /**
     * Initializes the predefined colors for tile actions.
     */
    private void initializeActionColors() {
        actionColors.put("LadderTileAction", Color.web("#d0f0c0")); // Light green
        actionColors.put("SnakeTileAction", Color.web("#ffd1d1")); // Light red
        actionColors.put("RandomTeleportTileAction", Color.LIGHTBLUE);
        actionColors.put("MissingTurnTileAction", Color.LIGHTSALMON);
        actionColors.put("ExtraThrowAction", Color.MEDIUMPURPLE);
        actionColors.put("GoToStartTileAction", Color.LIGHTCORAL);
        actionColors.put("SwapAction", Color.GOLD);

        actionColors.put("LadderDestination", Color.web("#90EE90")); // Lighter green
        actionColors.put("SnakeDestination", Color.web("#FFC0CB")); // Light pink
    }

    /**
     * Gets the color for a player, generating a new one if needed.
     *
     * @param player The player to get the color for
     * @return The color for the player
     */
    public Color getPlayerColor(Player player) {
        if (!playerColors.containsKey(player)) {
            logger.info("No color found for player " + player.getName() + ", generating a default color");
            playerColors.put(player, generatePlayerColor(player));
        } else {
            logger.info("Using existing color for player " + player.getName() + ": " + playerColors.get(player));
        }
        return playerColors.get(player);
    }

    /**
     * Sets a specific color for a player.
     *
     * @param player The player to set the color for
     * @param color The color to set
     */
    public void setPlayerColor(Player player, Color color) {
        playerColors.put(player, color);
    }

    /**
     * Sets a player's color from a hex color string.
     *
     * @param player The player to set the color for
     * @param colorHex The hex color string (e.g., "#FF0000")
     * @return True if the color was set successfully
     */
    public boolean setPlayerColorFromHex(Player player, String colorHex) {
        try {
            Color color = Color.web(colorHex);
            playerColors.put(player, color);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a consistent color for a player based on their name.
     *
     * @param player The player to generate a color for
     * @return A Color object for the player
     */
    public Color generatePlayerColor(Player player) {
        Random random = new Random(player.getName().hashCode());
        return Color.hsb(random.nextDouble() * 360, 0.8, 0.9);
    }

    /**
     * Gets the color for a tile based on its action.
     *
     * @param action The action of the tile
     * @return The color for the tile
     */
    public Color getTileColor(TileAction action) {
        if (action == null) {
            return Color.BEIGE;
        }

        if (action instanceof LadderTileAction) {
            return actionColors.get("LadderTileAction");
        } else if (action instanceof SnakeTileAction) {
            return actionColors.get("SnakeTileAction");
        } else if (action instanceof RandomTeleportTileAction) {
            return actionColors.get("RandomTeleportTileAction");
        } else if (action instanceof MissingTurnTileAction) {
            return actionColors.get("MissingTurnTileAction");
        } else if (action instanceof ExtraThrowAction) {
            return actionColors.get("ExtraThrowAction");
        } else if (action instanceof GoToStartTileAction) {
            return actionColors.get("GoToStartTileAction");
        } else if (action instanceof SwapAction) {
            return actionColors.get("SwapAction");
        }

        logger.warning("Unknown Tile action: " + action.getClass().getName());
        return Color.BEIGE;
    }

    /**
     * Gets the color for a ladder destination tile.
     *
     * @return The color for a ladder destination tile
     */
    public Color getLadderDestinationColor() {
        return actionColors.get("LadderDestination");
    }

    /**
     * Gets the color for a snake destination tile.
     *
     * @return The color for a snake destination tile
     */
    public Color getSnakeDestinationColor() {
        return actionColors.get("SnakeDestination");
    }

}
