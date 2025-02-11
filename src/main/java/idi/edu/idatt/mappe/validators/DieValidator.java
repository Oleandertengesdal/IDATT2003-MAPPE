package idi.edu.idatt.mappe.validators;

import idi.edu.idatt.mappe.models.dice.Die;

import java.util.List;

public class DieValidator {

    public static void validateDieSides(int sides) {
        if (sides < 1) {
            throw new IllegalArgumentException("Die number cannot be less than 1");
        }
    }

    public static void validateNumberOfDice(int numberOfDice, int numberOfSides) {
        if (numberOfDice < 1) {
            throw new IllegalArgumentException("Number of dice cannot be less than 1");
        }
        if (numberOfSides < 1) {
            throw new IllegalArgumentException("Die number cannot be less than 1");
        }
    }

    public static void validateGetDice(int diceIndex, List<Die> dice) {
        if (diceIndex < 0 || diceIndex >= dice.size()) {
            throw new IllegalArgumentException("Invalid dice index");
        }
    }
}
