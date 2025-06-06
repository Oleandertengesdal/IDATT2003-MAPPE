package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * A class representing a random teleport tile action
 */
public class RandomTeleportTileAction implements TileAction {

    private static final Logger logger = Logger.getLogger(RandomTeleportTileAction.class.getName());

    private Board board;
    private Random random;
    private String description;

    /**
     * Creates a new random teleport tile action
     *
     * @param board The board to teleport on
     */
    public RandomTeleportTileAction(Board board, String description) {
        super();
        this.board = board;
        this.random = new Random();
        this.description = description;
    }


    /**
     * Returns the description of the action
     *
     * @return The description of the action
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Places the player on a random tile
     *
     * @param player The player to move
     */
    @Override
    public void perform(Player player) {
        Map<Integer, Tile> boardTiles = board.getTiles();
        Tile randomTile = boardTiles.get( 5 + random.nextInt(boardTiles.size() - 10));
        logger.info("Player " + player.getName() + " was teleported from " + player.getCurrentTile() + " to " + randomTile.getIndex());
        player.placeOnTile(randomTile);
    }
}
