package idi.edu.idatt.mappe.validators;

import idi.edu.idatt.mappe.models.tileaction.TileAction;

/**
 * A class for validating tile numbers
 */
public class TileValidator {

    /**
     * Validates that the tile number is greater than 0
     *
     * @param tile The tile number to validate
     */
    public static void validateTile(int tile) {
        if (tile < 1) {
            throw new IllegalArgumentException("Tile number cannot be less than 1");
        }
    }

    /**
     * Validates that the tile index is within the board size
     *
     * @param tileIndex The index of the tile
     * @param size The size of the board
     */
    public static void validateTileIndex(int tileIndex, int size) {
        if (tileIndex < 0 || tileIndex >= size) {
            throw new IllegalArgumentException("Invalid tile index");
        }
    }

    /**
     * Validates that the tile action is not null
     *
     * @param tileAction The tile action to validate
     */
    public static void validateTileAction(TileAction tileAction) {
        if (tileAction == null) {
            throw new IllegalArgumentException("Tile action cannot be null");
        }
    }
}
