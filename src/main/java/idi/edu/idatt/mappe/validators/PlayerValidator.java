package idi.edu.idatt.mappe.validators;

import idi.edu.idatt.mappe.models.Player;

public class InvalidPlayerException extends Exception {
    public static void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
    }
}