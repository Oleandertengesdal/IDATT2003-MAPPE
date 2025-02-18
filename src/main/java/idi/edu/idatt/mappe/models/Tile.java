package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.tileaction.TileAction;

import static idi.edu.idatt.mappe.validators.PlayerValidator.validatePlayer;
import static idi.edu.idatt.mappe.validators.TileValidator.validateTileAction;

/**
 * Represents a tile on the board
 *
 *
 */
public class Tile {
    private Tile nextTile;
    private int tileId;
    private TileAction landAction;

    /**
     * Creates a new tile with the given id
     *
     * @param tileId The id of the tile
     */
    public Tile(int tileId) {
        this.tileId = tileId;
    }

    /**
     * Sets the action to be performed when a player lands on this tile
     *
     * @param player
     */
    public void landPlayer(Player player) {
        validatePlayer(player);
        validateTileAction(landAction);
        landAction.perform(player);
    }

    /**
     * Sets the action to be performed when a player leaves this tile
     *
     * @param player
     */
    public void leavePlayer(Player player) {
        // TODO: Implement
    }

    /**
     * Returns the next tile
     *
     * @return The next tile
     */
    public Tile getNextTile() {
        return nextTile;
    }

    /**
     * Returns the next tile
     *
     * @return The next tile
     */
    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }

    /**
     * Sets the action to be performed when a player lands on this tile
     *
     * @param action The action to be performed
     */
    public void setLandAction(TileAction action) {
        validateTileAction(action);
        this.landAction = action;
    }

    /**
     * Performs the action to be performed when a player lands on this tile
     *
     * @param player The player that landed on the tile
     */
    public void performLandAction(Player player) {
        validatePlayer(player);
        validateTileAction(landAction);
        landAction.perform(player);
    }

    /**
     * Gets the action to be performed when a player lands on this tile
     *
     * @return The action to be performed
     */
    public TileAction getLandAction() {
        return landAction;
    }

    /**
     *  Returns the index of the tile
     *
     * @return The index of the tile
     */
    public int getIndex() {
        return tileId;
    }
}
