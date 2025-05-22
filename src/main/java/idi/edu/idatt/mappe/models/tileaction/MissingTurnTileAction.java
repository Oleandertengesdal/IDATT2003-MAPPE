package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;

/**
 * Represents an action that is performed when a player lands on a tile
 * <p>
 *     This action allows the player to miss a turn
 *     if the player lands on the tile
 */
public class MissingTurnTileAction implements TileAction {

    private final String description;
    private final Board board;

    /**
     * Creates a new missing turn tile action
     */
    public MissingTurnTileAction(String description, Board board) {
        super();
        this.description = description;
        this.board = board;
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
     *     This action allows the player to miss a turn
     *
     * @param player The player to move
     */
    @Override
    public void perform(Player player) {
        player.setMissingTurn(true);
    }
}
