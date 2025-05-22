package idi.edu.idatt.mappe.models;

import idi.edu.idatt.mappe.models.enums.TokenType;

/**
 * Observer interface specific to The Lost Diamond game events
 */
public interface LostDiamondObserver extends BoardGameObserver {
    /**
     * Called when a token is revealed
     *
     * @param player The player who revealed the token
     * @param tokenType The type of token revealed
     */
    void onTokenRevealed(Player player, TokenType tokenType);

    /**
     * Called when the diamond is found
     *
     * @param player The player who found the diamond
     */
    void onDiamondFound(Player player);
}