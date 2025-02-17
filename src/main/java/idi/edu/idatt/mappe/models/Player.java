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
     */
    public Player(String name) {
        this.name = name;
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
     * Returns the board of the game
     *
     * @return The board of the game
     */
    public Board getBoard() {
        return game.getBoard();
    }

    /**
     * Sets the BoardGame of the player
     *
     * @param game The game the player is playing
     */

    public void setGame(BoardGame game) {
        this.game = game;
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
        if (currentTile == null) {
            throw new IllegalStateException("The player must be placed on a tile before moving");
        }
        Tile newTile = currentTile;
        for (int i = 0; i < steps; i++) {
            if (newTile.getNextTile() == null) {
                // Player has reached or passed the last tile
                System.out.println(name + " has reached the end of the game!");
                return;
            }
            newTile = newTile.getNextTile();
        }
        currentTile = newTile;
    }
}
