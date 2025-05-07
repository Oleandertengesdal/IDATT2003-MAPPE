package idi.edu.idatt.mappe.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Represents a player entry in the player selection view.
 * This class wraps a Player object and provides additional properties.
 * It includes a selection property and a color property.
 */
public class PlayerSelectionEntry {
    private final Player player;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private String color;

    /**
     * Creates a new PlayerSelectionEntry for the given player.
     *
     * @param player The player to wrap
     */
    public PlayerSelectionEntry(Player player) {
        this.player = player;

        // Assign a default color based on the player's name hash
        String[] availableColors = {
                "#e74c3c", "#3498db", "#2ecc71", "#f1c40f", "#9b59b6",
                "#e67e22", "#1abc9c", "#34495e", "#d35400", "#16a085"
        };

        this.color = availableColors[Math.abs(player.getName().hashCode()) % availableColors.length];
    }

    /**
     * Gets the underlying player.
     *
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name
     */
    public String getName() {
        return player.getName();
    }

    /**
     * Gets the player's token.
     *
     * @return The player's token
     */
    public String getToken() {
        return player.getToken();
    }

    /**
     * Sets the player's token.
     *
     * @param token The token to set
     */
    public void setToken(String token) {
        player.setToken(token);
    }

    /**
     * Gets the player's color.
     *
     * @return The player's color hex code
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the player's color.
     *
     * @param color The color hex code to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Checks if this player is selected.
     *
     * @return True if the player is selected, false otherwise
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Property for selection status.
     *
     * @return The selection property
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Sets the selection status.
     *
     * @param selected True to select the player, false to deselect
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

}