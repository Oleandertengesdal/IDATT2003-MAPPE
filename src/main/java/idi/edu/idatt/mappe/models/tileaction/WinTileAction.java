package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.enums.GameState;
import idi.edu.idatt.mappe.models.Player;

/**
 * The WinTileAction class represents a tile action that allows players to win the game.
 * It implements the TileAction interface.
 * <p>
 *     This class is responsible for checking if all players have reached home and
 *     setting the game state to finished if they have.
 *     This is used in the LUDO game.
 * </p>
 */
public class WinTileAction implements TileAction {

    private final String description;
    private final Board board;

    /**
     * Creates a new WinTileAction with the given description and board
     *
     * @param description The description of the action
     * @param board The board of this tile action
     */
    public WinTileAction(String description, Board board) {
        this.description = description;
        this.board = board;
    }

    /**
     * Method to perform the tileaction.
     * This action is responsible for checking if all players have reached home and
     * setting the game state to finished if they have.
     *
     * @param player The player who landed on the tile
     */
    @Override
    public void perform(Player player) {

    }

    /**
     * Method to return the description of the Tileaction.
     *
     * @return The description of the Tileaction.
     */
    @Override
    public String getDescription() {
        return description;
    }
}
