package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;

/**
 * Represents an action that is performed when a player lands on a tile
 * <p>
 *     This action allows the player to miss a turn
 *     if the player lands on the tile
 */
public class MissingTurnTileAction implements TileAction {

    private final String description;

    /**
     * Creates a new missing turn tile action
     */
    public MissingTurnTileAction(String description) {
        super();
        this.description = description;
    }

    /**
     * Returns the description of the action
     *
     * @return The description of the action
     */
    public String getDescription() {
        return description;
    }

    /**
     * Performs the action
     * <p>
     *     This action allows the player to miss a turn
     *
     * @param player The player to move
     */
    @Override
    public void perform(Player player) {
        // player.setMissingTurn(true);
    }
}
