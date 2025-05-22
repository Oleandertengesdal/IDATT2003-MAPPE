package idi.edu.idatt.mappe.models.enums;

/**
 * Represents the type of token on the board
 * <p>
 *     This is going to be used in The Lost Diamond, where we have different types of tokens.
 *     The tokens are representing the cardboard piece that the player is flipping when landing on a CITY.
 * <p>
 * @version 1.0
 */
public enum TokenType {
    DIAMOND(5000, "The Lost Diamond"),
    RUBY(1000, "Ruby"),
    EMERALD(500, "Emerald"),
    TOPAZ(300, "Topaz"),
    THIEF(0, "Thief - lose treasures!"),
    EMPTY(0, "Nothing here!");

    private final int value;
    private final String description;

    /**
     * Constructor for the TokenType enum
     *
     * @param value The value of the token
     * @param description The description of the token
     */
    TokenType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * Returns the value of the token
     *
     * @return The value of the token
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the description of the token
     *
     * @return The description of the token
     */
    public String getDescription() {
        return description;
    }
}
