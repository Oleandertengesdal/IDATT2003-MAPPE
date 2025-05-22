package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;

/**
 * Represents an action that is performed when a player lands on a tile
 */
public interface TileAction {
    /**
     * Performs the action
     *
     * @param player The player to move
     */
    void perform(Player player);

    String getDescription();
}
