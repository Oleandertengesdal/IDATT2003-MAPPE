package idi.edu.idatt.mappe.models.dice;

import java.util.Random;

/**
 * The Die class that represents a six-sized die.
 * It can be rolled to generate a random value between 1 and 6.
 * <p>
 * The class utilizes {@link java.util.Random} to generate random values.
 * </p>
 */
public class Die {
    private int lastRolledValue;
    private final Random random;

    /**
     * Creates a new Die object and rolls it once upon initialization.
     */    public Die() {
        random = new Random();
        lastRolledValue = random.nextInt(6) + 1;
    }

    /**
     * Rolls the die and updates its value with a random number between 1 and 6.
     *
     * @return the new value of the die after rolling..
     */
    public int roll() {
        lastRolledValue = random.nextInt(6) + 1;
        return lastRolledValue;
    }

    /**
     * Retrieves the last rolled value of the die.
     *
     * @return the most recent value of the die.
     */
    public int getValue() {
        return lastRolledValue;
    }
}
