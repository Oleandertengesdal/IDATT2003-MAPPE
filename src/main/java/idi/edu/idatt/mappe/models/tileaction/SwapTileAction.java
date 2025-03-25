package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.Board;
import idi.edu.idatt.mappe.models.BoardGame;
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
    private BoardGame game;
    private Random random;

    /**
     * Creates a new swap tile action
     *
     * @param description The description of the action
     * @param game The Boardgame
     */
    public SwapTileAction(BoardGame game, String description) {
        this.description = description;
        this.game = game;
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
     * Returns the game of the action
     *
     * @return The game of the action
     */
    public BoardGame getGame() {
        return game;
    }

    public void setGame(BoardGame game) {
        this.game = game;
    }

    /**
     * Swaps the position of the players
     */
    @Override
    public void perform(Player player) {
        List<Player> players = game.getPlayers();
        if (players.size() > 1) {
            Player otherPlayer;
            do {
                otherPlayer = players.get(random.nextInt(players.size()));
            } while (otherPlayer == player);

            // Swap positions
            int playerTileIndex = player.getCurrentTile().getIndex();
            int otherPlayerTileIndex = otherPlayer.getCurrentTile().getIndex();

            player.placeOnTile(game.getBoard().getTileByIndex(otherPlayerTileIndex));
            otherPlayer.placeOnTile(game.getBoard().getTileByIndex(playerTileIndex));
        }
    }
}
