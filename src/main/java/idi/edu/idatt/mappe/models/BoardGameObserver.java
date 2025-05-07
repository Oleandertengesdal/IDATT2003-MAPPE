package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.GameState;

public interface BoardGameObserver {
    /**
     * Called when a player moves
     * @param player The player who moved
     * @param steps The number of steps moved
     */
    void onPlayerMoved(Player player, int steps);

    /**
     * Called when the game state changes
     * @param gameState The current state of the game
     */
    void onGameStateChanged(GameState gameState);

    /**
     * Called when a winner is determined
     * @param winner The winning player
     */
    void onGameWinner(Player winner);


    /**
     * Called when a player is captured
     *
     * @param captor The player who captured
     * @param victim The player who was captured
     */
    void onPlayerCaptured(Player captor, Player victim);

}
