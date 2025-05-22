package idi.edu.idatt.mappe.models.tileaction;


import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

public class StartingAreaTileAction implements TileAction {

    private final String description;
    private final Board board;

    private static final Logger logger = getLogger(StartingAreaTileAction.class.getName());

    /**
     * Creates a new StartingAreaTileAction with the given description and tile
     *
     * @param description The description of the action
     * @param board The tile of this tile action
     */
    public StartingAreaTileAction(String description, Board board) {
        this.description = description;
        this.board = board;
    }

    /**
     * Method to perform the tileaction.
     * This action is responsible for moving the player to the starting area.
     *
     * @param player The player who landed on the tile
     */
    @Override
    public void perform(Player player) {
        logger.info("The player " + player.getName() + " has landed on the starting area tile");
    }

    /**
     * Method to return the description of the tileaction.
     *
     * @return The description of the tileaction.
     */
    @Override
    public String getDescription() {
        return description;
    }
}
