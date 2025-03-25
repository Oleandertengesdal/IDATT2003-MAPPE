package idi.edu.idatt.mappe.models;

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
}
