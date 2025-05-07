package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;

/**
 * Represents an action that is performed when a player lands on a tile
 * <p>
 *     This action allows the player to throw the dice again
 *     if the player lands on the tile
 */
public class ExtraThrowAction implements TileAction {

    private final String description;
    /**
     * Creates a new extra throw tile action
     */
    public ExtraThrowAction(String description, Board board) {
        super();
        this.description = description;
    }

    /**
     * Returns the description of the action
     *
     * @return The description of the action
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Performs the action
     * <p>
     *     This action allows the player to throw the dice again
     *
     * @param player The player to move
     */
    @Override
    public void perform(Player player) {
        // player.setExtraThrow(true);
    }
}
