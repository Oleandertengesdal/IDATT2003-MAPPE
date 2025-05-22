package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.TokenType;

import java.util.logging.Logger;

/**
 * Represents a player in a board game.
 */
public class Player {

    private String name;
    private String token;
    private Tile currentTile;
    private BoardGame game;

    private boolean hasReachedHome;

    private boolean extraThrow;
    private boolean missingTurn;

    private int money;
    private int startingMoney = 300;
    private boolean hasDiamond = false;


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

    /**
     * Creates a new player with the given name, token, and money
     *
     * @param name The player's name
     * @param token The player's token
     * @param startingMoney The amount of money to start with
     */
    public Player(String name, String token, int startingMoney) {
        this.name = name;
        this.token = token;
        this.startingMoney = startingMoney;
        this.money = startingMoney;
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
        return token;
    }

    /**
     * Sets the token of the player
     *
     * @param token The token of the player
     */
    public void setToken(String token) {
        this.token = token;
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
                logger.warning(name + " has reached the end of the game!");
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

    public void setCurrentTile(Tile nextTile) {
        this.currentTile = nextTile;
    }

    /**
     * Gets the player's current money
     *
     * @return The player's money
     */
    public int getMoney() {
        return money;
    }

    /**
     * Sets the player's money
     *
     * @param money The new amount of money
     */
    public void setMoney(int money) {
        int oldMoney = this.money;
        this.money = money;
        logger.info(name + " money changed from " + oldMoney + " to " + money);
    }

    /**
     * Gets the player's starting money amount
     *
     * @return The starting money amount
     */
    public int getStartingMoney() {
        return startingMoney;
    }

    /**
     * Adds money to the player's total
     *
     * @param amount The amount to add
     */
    public void addMoney(int amount) {
        this.money += amount;
        logger.info(name + " gained " + amount + " coins. New balance: " + money);
    }

    /**
     * Spends money if the player has enough
     *
     * @param amount The amount to spend
     */
    public boolean spendMoney(int amount) {
        if (money >= amount) {
            money -= amount;
            logger.info(name + " spent " + amount + " coins. New balance: " + money);
            return true;
        }
        logger.warning(name + " tried to spend " + amount + " coins but only has " + money);
        return false;
    }

    /**
     * Checks if the player has the diamond
     *
     * @return True if the player has the diamond
     */
    public boolean hasDiamond() {
        return hasDiamond;
    }

    /**
     * Sets whether the player has the diamond
     *
     * @param hasDiamond True if the player has the diamond
     */
    public void setHasDiamond(boolean hasDiamond) {
        this.hasDiamond = hasDiamond;
    }

    /**
     * Reveals the token at the current tile if it's a city
     *
     * @return The revealed TokenType, or null if not possible
     */
    public TokenType revealToken() {
        if (currentTile != null && currentTile.isCity() && currentTile.hasToken()) {
            return currentTile.revealToken(this);
        }
        return null;
    }

    /**
     * Checks if the player has won (has diamond and returned to starting city)
     *
     * @return True if the player has won
     */
    public boolean hasWon() {
        return hasDiamond && currentTile != null && currentTile.isStartingCity();
    }
    /**
     * Resets the player for a new game
     */
    public void reset() {
        this.currentTile = null;
        this.hasReachedHome = false;
        this.extraThrow = false;
        this.missingTurn = false;
        this.money = startingMoney;
        this.hasDiamond = false;
    }

}
