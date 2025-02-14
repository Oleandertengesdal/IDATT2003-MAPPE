package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;

public class SnakeTileAction implements TileAction {

    private final int destinationTileId;
    private final String description;

    /**
     * Creates a new snake tile action with the given destination
     *
     * @param destinationTileId The destination of the snake
     */
    public SnakeTileAction(int destinationTileId, String description) {
        this.destinationTileId = destinationTileId;
        this.description = description;
    }

    /**
     * Returns the destination of the snake
     *
     * @return The destination of the snake
     */
    public int getDestinationTileId() {
        return destinationTileId;
    }

    /**
     * Returns the description of the snake
     *
     * @return The description of the snake
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
        player.placeOnTile(destinationTileId);
    }
}
