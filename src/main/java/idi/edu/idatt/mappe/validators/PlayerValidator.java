package idi.edu.idatt.mappe.validators;
import idi.edu.idatt.mappe.models.Player;

/**
 * A class for validating players
 */
public class PlayerValidator {

    public static void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }
}