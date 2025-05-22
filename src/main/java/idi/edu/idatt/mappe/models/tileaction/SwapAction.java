package idi.edu.idatt.mappe.models.tileaction;

import idi.edu.idatt.mappe.models.BoardGame;
import idi.edu.idatt.mappe.models.Player;

import java.util.List;
import java.util.Random;

/**
 * Represents a tile action that swaps the position of the players
 * <p>
 *     This action swaps the position of the players that lands on the tile
 */
public class SwapAction implements TileAction {
    private String description;
    private BoardGame game;
    private Random random;

    /**
     * Creates a new swap tile action
     */
    public SwapAction(BoardGame game, String description) {
        this.description = description;
        this.game = game;
        this.random = new Random();
    }

    @Override
    public String getDescription() {
        return description;
    }

    public BoardGame getGame() {
        return game;
    }

    public void setGame(BoardGame game) {
        this.game = game;
    }

    @Override
    public void perform(Player player) {
        if (game == null) {
            game = player.getGame();
            if (game == null) {
                return;
            }
        }

        List<Player> players = game.getPlayers();
        if (players.size() > 1) {
            Player otherPlayer;
            do {
                otherPlayer = players.get(random.nextInt(players.size()));
            } while (otherPlayer == player);

            int playerTileIndex = player.getCurrentTile().getIndex();
            int otherPlayerTileIndex = otherPlayer.getCurrentTile().getIndex();

            player.placeOnTile(game.getBoard().getTileByIndex(otherPlayerTileIndex));
            otherPlayer.placeOnTile(game.getBoard().getTileByIndex(playerTileIndex));

            if (game.getCurrentPlayer() == player) {
                game.notifyObserversOfSwap(player, otherPlayer, playerTileIndex, otherPlayerTileIndex);
            }
        }
    }
}
