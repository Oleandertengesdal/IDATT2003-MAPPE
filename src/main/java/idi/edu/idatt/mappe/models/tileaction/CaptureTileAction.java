package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

/**
 * The CaptureTileAction class represents a tile action that captures other players.
 * It implements the TileAction interface.
 * <p>
 *     * This class is responsible for capturing other players when they land on a tile
 *     that has this tileaction.
 * </p>
 */
public class CaptureTileAction implements TileAction {

    private final static Logger logger = getLogger(CaptureTileAction.class.getName());

    private final String description;
    private final Board board;

    /**
     * Creates a new CaptureTileAction with the given description and board
     *
     * @param description The description of the action
     * @param board The board of this tile action
     */
    public CaptureTileAction(String description, Board board) {
        this.description = description;
        this.board = board;
    }


    /**
     *  Method to perform the tileaction.
     *  this action is responsible for capturing other players.
     *
     * @param player The player who landed on the tile
     */
    @Override
    public void perform(Player player) {

    }

    /**
     * Method to return the description of the tileaciton.
     *
     * @return The description of the tileaction.
     */
    @Override
    public String getDescription() {
        return description;
    }
}
