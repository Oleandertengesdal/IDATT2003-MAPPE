package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.Direction;
import idi.edu.idatt.mappe.models.enums.GameType;
import idi.edu.idatt.mappe.models.enums.TileType;
import idi.edu.idatt.mappe.models.enums.TokenType;
import idi.edu.idatt.mappe.models.tileaction.TileAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static idi.edu.idatt.mappe.validators.PlayerValidator.validatePlayer;
import static idi.edu.idatt.mappe.validators.TileValidator.validateTileAction;

/**
 * Represents a tile on the board
 *
 * This class is responsible for managing the tile's properties, such as its ID, coordinates,
 * connections to other tiles, and the action that occurs when a player lands on it.
 *
 * @version 1.1
 * @see TileAction
 */
public class Tile {

    private Tile nextTile;
    private int tileId;
    private TileAction landAction;

    private int x; // x-coordinate on the board
    private int y; // y-coordinate on the board

    private Map<Direction, Tile> connections = new HashMap<>();
    private Map<Direction, Integer> travelCosts = new HashMap<>();

    private boolean isStartTile = false;
    private boolean isGoal = false;
    private TileType tileType;
    private String name;
    private TokenType hiddenToken;
    private boolean tokenRevealed = false;
    private int tokenPrice = 100;

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
     * Creates a new tile for The Lost Diamond game
     *
     * @param tileId The id of the tile
     * @param name The name of the location
     * @param tileType The type of tile (city, sea route, etc.)
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public Tile(int tileId, String name, TileType tileType, int x, int y) {
        this.tileId = tileId;
        this.name = name;
        this.tileType = tileType;
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
        if (landAction != null && player.getGame().getGameType() == GameType.SNAKES_AND_LADDERS) {
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
     * Adds connections to another tile in a spoecific direction
     * implemented because of the need to have a tile with multiple connections
     *
     * @param direction The direction of the connection
     * @param tile The tile to connect to
     */
    public void addConnection(Direction direction, Tile tile, int cost) {
        connections.put(direction, tile);
        travelCosts.put(direction, cost);
    }

    /**
     * Gets the connected tile in the specified direction
     *
     * @param direction The direction to check
     * @return The connected tile, or null if no connection exists
     */
    public Tile getConnectionInDirection(Direction direction) {
        return connections.get(direction);
    }

    /**
     * Gets all available connections from this tile
     *
     * @return A map of direction to connected tiles
     */
    public Map<Direction, Tile> getConnections() {
        return new HashMap<>(connections);
    }

    /**
     * Gets a list of all available directions from this tile
     *
     * @return List of available directions
     */
    public List<Direction> getAvailableDirections() {
        return new ArrayList<>(connections.keySet());
    }

    /**
     * Checks if this tile is the goal (contains the diamond)
     *
     * @return True if this is the goal tile
     */
    public boolean isGoal() {
        return isGoal;
    }

    /**
     * Sets whether this tile is the goal tile
     *
     * @param isGoal True if this is the goal tile
     */
    public void setGoal(boolean isGoal) {
        this.isGoal = isGoal;
    }

    /**
     * Checks if this tile is a starting position
     *
     * @return True if this is a starting position
     */
    public boolean isStart() {
        return isStartTile;
    }

    /**
     * Sets whether this tile is a starting position
     *
     * @param isStartTile True if this is a starting position
     */
    public void setStart(boolean isStartTile) {
        this.isStartTile = isStartTile;
    }

    /**
     * Gets the cost to travel in a specific direction
     *
     * @param direction The travel direction
     * @return The cost in coins, or 0 if free/not available
     */
    public int getTravelCost(Direction direction) {
        return travelCosts.getOrDefault(direction, 0);
    }

    /**
     * Checks if this tile has a hidden token (cities only)
     *
     * @return True if this tile has a token
     */
    public boolean hasToken() {
        return tileType == TileType.CITY && hiddenToken != null && !tokenRevealed;
    }

    /**
     * Gets the cost to buy/reveal the token at this location
     *
     * @return The token price
     */
    public int getTokenPrice() {
        return tokenPrice;
    }

    /**
     * Sets the token price for this city
     *
     * @param price The price to set
     */
    public void setTokenPrice(int price) {
        this.tokenPrice = price;
    }

    /**
     * Sets the hidden token for this tile
     *
     * @param tokenType The token to hide
     */
    public void setHiddenToken(TokenType tokenType) {
        this.hiddenToken = tokenType;
    }

    /**
     * Reveals the token at this location
     *
     * @param player The player revealing the token
     * @return The revealed token type, or null if already revealed or not a token tile
     */
    public TokenType revealToken(Player player) {
        tokenRevealed = true;
        if (landAction == null) {
            return hiddenToken;
        }
        landAction.perform(player);
        return hiddenToken;
    }

    /**
     * Gets the tile type
     *
     * @return The tile type
     */
    public TileType getTileType() {
        return tileType;
    }

    /**
     * Gets the name of this location
     *
     * @return The location name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if this is a city tile
     *
     * @return True if this is a city
     */
    public boolean isCity() {
        return tileType == TileType.CITY || tileType == TileType.STARTING_CITY;
    }

    /**
     * Checks if this is a starting city (Cairo or Tangier)
     *
     * @return True if this is a starting city
     */
    public boolean isStartingCity() {
        return tileType == TileType.STARTING_CITY;
    }

    /**
     * Gets a list of connected tiles that the player can afford to travel to
     *
     * @param player The player
     * @return List of affordable connected tiles
     */
    public List<Tile> getAffordableConnections(Player player) {
        List<Tile> affordableTiles = new ArrayList<>();

        for (Map.Entry<Direction, Tile> entry : connections.entrySet()) {
            Direction direction = entry.getKey();
            Tile connectedTile = entry.getValue();
            int cost = getTravelCost(direction);

            if (cost <= player.getMoney()) {
                affordableTiles.add(connectedTile);
            }
        }

        return affordableTiles;
    }
}