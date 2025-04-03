package idi.edu.idatt.mappe.models;

import java.util.logging.Logger;

/**
 * Represents a player in a board game.
 */
public class Player {

    private String name;
    private String token;
    private Tile currentTile;
    private BoardGame game;
    private boolean extraThrow;
    private boolean missingTurn;

    private static final Logger logger = Logger.getLogger(Player.class.getName());

    /**
     * Creates a new player with the given name and game
     *
     * @param name The name of the player
     */
    public Player(String name, String token) {
        this.name = name;
        this.token = token;
        this.missingTurn = false;
        this.extraThrow = false;
    }

    public Player(String name) {
        this(name, "default");
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
     * Returns the token of the player
     *
     * @return The token of the player
     */
    public String getToken() {
        return null;
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
     * Returns the BoardGame of the player
     *
     * @return The game the player is playing
     */
    public BoardGame getGame() {
        return game;
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
        tile.landPlayer(this);
    }

    /**
     * Returns whether the player has an extra throw
     *
     * @return Whether the player has an extra throw
     */
    public boolean hasExtraThrow() {
        return extraThrow;
    }

    /**
     * Sets whether the player has an extra throw
     *
     * @param extraThrow Whether the player has an extra throw
     */
    public void setExtraThrow(boolean extraThrow) {
        this.extraThrow = extraThrow;
    }

    /**
     * Returns whether the player is missing a turn
     *
     * @return Whether the player is missing a turn
     */
    public boolean isMissingTurn() {
        return missingTurn;
    }

    /**
     * Sets whether the player is missing a turn
     *
     * @param missingTurn Whether the player is missing a turn
     */
    public void setMissingTurn(boolean missingTurn) {
        this.missingTurn = missingTurn;
    }



    /**
     * Moves the player a given number of steps
     *
     * @param steps The number of steps to move
     */
    public void move(int steps) {
        if(missingTurn){
            logger.info(name + " is missing a turn");
            missingTurn = false;
        } else {
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

    /**
     * Returns the index of the current tile
     *
     * @return The index of the current tile
     */
    public int getCurrentTileIndex() {
        return currentTile.getIndex();
    }
}
