package idi.edu.idatt.mappe.services;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.tileaction.TileAction;
import idi.edu.idatt.mappe.models.tileaction.LadderTileAction;
import idi.edu.idatt.mappe.models.tileaction.SnakeTileAction;
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

    private static final List<String> DEFAULT_PLAYER_COLORS = List.of(
            "#e74c3c", "#3498db", "#2ecc71", "#f1c40f", "#9b59b6",
            "#e67e22", "#1abc9c", "#34495e", "#d35400", "#16a085"
    );

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
        actionColors.put("SkipTurnTileAction", Color.LIGHTSALMON);
        actionColors.put("ExtraThrowAction", Color.LIGHTYELLOW);
        actionColors.put("GoToStartTileAction", Color.LIGHTCORAL);
        actionColors.put("SwapAction", Color.GOLD);
        actionColors.put("StartingAreaTileAction", Color.LIGHTGRAY);
        actionColors.put("SafeZoneTileAction", Color.LIGHTGREEN);
        actionColors.put("WinTileAction", Color.GOLD);

        // Ladder and snake destination colors
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

        String actionType = action.getClass().getSimpleName();
        return actionColors.getOrDefault(actionType, Color.BEIGE);
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

    /**
     * Gets the list of default player colors in hex format.
     *
     * @return A list of hex color strings
     */
    public List<String> getDefaultPlayerColors() {
        return DEFAULT_PLAYER_COLORS;
    }

    /**
     * Gets a default player color by index.
     *
     * @param index The index of the color
     * @return The hex color string
     */
    public String getDefaultPlayerColor(int index) {
        return DEFAULT_PLAYER_COLORS.get(index % DEFAULT_PLAYER_COLORS.size());
    }

    /**
     * Gets a random default player color.
     *
     * @return A random hex color string
     */
    public String getRandomDefaultPlayerColor() {
        Random random = new Random();
        return DEFAULT_PLAYER_COLORS.get(random.nextInt(DEFAULT_PLAYER_COLORS.size()));
    }

    /**
     * Returns the color for a safe zone tile
     *
     * @return The color for a safe zone tile
     */
    public Color getSafeZoneColor() {
        return Color.LIGHTGREEN;
    }

    /**
     * Returns the color for a starting area tile
     *
     * @return The color for a starting area tile
     */
    public Color getStartingAreaColor() {
        return actionColors.get("StartingAreaTileAction");
    }

    /**
     * Returns the color for a home/winning tile
     *
     * @return The color for a home/winning tile
     */
    public Color getHomeColor() {
        return Color.GOLD;
    }

    public Color getWinTileColor() {
        return actionColors.get("WinTileAction");
    }
}
