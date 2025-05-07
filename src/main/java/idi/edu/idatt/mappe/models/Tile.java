package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.tileaction.TileAction;

import static idi.edu.idatt.mappe.validators.PlayerValidator.validatePlayer;
import static idi.edu.idatt.mappe.validators.TileValidator.validateTileAction;

/**
 * Represents a tile on the board
 */
public class Tile {

    private Tile nextTile;
    private int tileId;
    private TileAction landAction;

    // Values for LUDO game.
    private boolean safeZone = false;
    private boolean isDisabled = false;

    private int x; // x-coordinate on the board
    private int y; // y-coordinate on the board

    /**
     * Creates a new tile with the given id
     *
     * @param tileId The id of the tile
     */
    public Tile(int tileId) {
        this.tileId = tileId;
    }

    /**
     * Creates a new tile with the given id and coordinates
     *
     * @param tileId The id of the tile
     * @param x The x-coordinate of the tile
     * @param y The y-coordinate of the tile
     */
    public Tile(int tileId, int x, int y) {
        this.tileId = tileId;
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the action to be performed when a player lands on this tile
     *
     * @param player The player who landed on the tile
     */
    public void landPlayer(Player player) {
        validatePlayer(player);
        validateTileAction(landAction);
        if (landAction != null) {
            System.out.println("Player " + player.getName() + " landed on tile " + tileId);
            landAction.perform(player);
        }
    }

    /**
     * Sets the action to be performed when a player leaves this tile
     *
     * @param player The player who is leaving the tile
     */
    public void leavePlayer(Player player) {
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
     * Sets the next tile
     *
     * @param nextTile The next tile
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
     * Returns the index of the tile
     *
     * @return The index of the tile
     */
    public int getIndex() {
        return tileId;
    }

    /**
     * Returns the x-coordinate of the tile
     *
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the tile
     *
     * @param x The x-coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of the tile
     *
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the tile
     *
     * @param y The y-coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets both x and y coordinates of the tile
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns whether the tile is a safe zone
     *
     * @return True if the tile is a safe zone, false otherwise
     */
    public boolean isSafeZone() {
        return safeZone;
    }


    /**
     * Sets whether the tile is a safe zone
     *
     * @param safeZone True if the tile is a safe zone, false otherwise
     */
    public void setSafeZone(boolean safeZone) {
        this.safeZone = safeZone;
    }

    /**
     * Returns whether the tile is disabled
     *
     * @return True if the tile is disabled, false otherwise
     */
    public boolean isDisabled() {
        return isDisabled;
    }

    /**
     * Sets whether the tile is disabled
     *
     * @param isDisabled True if the tile is disabled, false otherwise
     */
    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
}