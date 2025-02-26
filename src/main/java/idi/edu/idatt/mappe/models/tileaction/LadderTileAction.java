package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;

public class LadderTileAction implements TileAction {

    private final int destinationTileId;
    private final String description;
    private Board board;

    /**
     * Creates a new ladder tile action with the given destination
     *
     * @param destinationTileId The destination of the ladder
     */
    public LadderTileAction(int destinationTileId, String description, Board board) {
        this.destinationTileId = destinationTileId;
        this.description = description;
        this.board = board;
    }

    /**
     * Returns the destination of the ladder
     *
     * @return The destination of the ladder
     */
    public int getDestinationTileId() {
        return destinationTileId;
    }

    /**
     * Returns the description of the ladder
     *
     * @return The description of the ladder
     */
    public String getDescription() {
        return description;
    }

    /**
     * Places the player on the destination tile
     *
     * @param player The player to move
     */
    @Override
    public void perform(Player player) {
        player.placeOnTile(board.getTile(destinationTileId));
    }
}
