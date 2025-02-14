package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Player;
import idi.edu.idatt.mappe.models.Tile;

import java.util.List;
import java.util.Random;

/**
 * Represents a tile action that swaps the position of the players
 * <p>
 *     This action swaps the position of the players that lands on the tile
 */
public class SwapTileAction implements TileAction {

    private String description;
    private List<Player> players;
    private Random random;

    /**
     * Creates a new swap tile action
     *
     * @param description The description of the action
     * @param players The players to swap
     */
    public SwapTileAction(List<Player> players, String description) {
        this.description = description;
        this.players = players;
        this.random = new Random();
    }

    /**
     * Returns the description of the action
     *
     * @return The description of the action
     */
    public String getDescription() {
        return description;
    }

    /**
     * Swaps the position of the players
     */
    @Override
    public void perform(Player player) {
        Tile playerTile = player.getCurrentTile();
        Player randomPlayer = players.get(random.nextInt(players.size() - 1));
        Tile randomPlayerTile = randomPlayer.getCurrentTile();
        player.placeOnTile(randomPlayerTile);
        randomPlayer.placeOnTile(playerTile);
    }
}
