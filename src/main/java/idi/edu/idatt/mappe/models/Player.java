package idi.edu.idatt.mappe.models;

/**
 * Represents a player in a board game.
 */
public class Player {

    private String name;
    private Tile currentTile;
    private BoardGame game;

    /**
     * Creates a new player with the given name and game
     *
     * @param name The name of the player
     * @param game The game the player is playing
     */
    public Player(String name, BoardGame game) {
        this.name = name;
        this.game = game;
    }

    /**
     * Returns the name of the player
     *
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current tile of the player
     *
     * @return The current tile of the player
     */
    public Tile getCurrentTile() {
        return currentTile;
    }

    /**
     * Sets the name of the player
     *
     * @param name The name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Places the player on the given tile
     *
     * @param tile The tile to place the player on
     */
    public void placeOnTile(Tile tile) {
        this.currentTile = tile;
    }

    /**
     * Moves the player a given number of steps
     *
     * @param steps The number of steps to move
     */
    public void move(int steps) {
        Tile newTile = currentTile;
        for (int i = 0; i < steps; i++) {
            newTile = newTile.nextTile;
        }
        currentTile = newTile;
        currentTile.landPlayer(this);
    }
}
