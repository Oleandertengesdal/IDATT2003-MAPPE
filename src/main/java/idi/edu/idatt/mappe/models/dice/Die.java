package idi.edu.idatt.mappe.models.dice;

import java.util.Random;
import java.util.logging.Logger;

import static idi.edu.idatt.mappe.validators.DieValidator.validateDieSides;

/**
 * The Die class that represents a six-sized die.
 * It can be rolled to generate a random value between 1 and 6.
 * <p>
 * The class utilizes {@link java.util.Random} to generate random values.
 * </p>
 */
public class Die {

    private static final Logger logger = Logger.getLogger(Die.class.getName());

    private int lastRolledValue;
    private final Random random;
    private final int numberOfSides;

    /**
     * Creates a new Die object and rolls it once upon initialization.
     */
    public Die(int numberOfSides) {
        validateDieSides(numberOfSides);
        this.numberOfSides = numberOfSides;
        random = new Random();
        lastRolledValue = random.nextInt(numberOfSides) + 1;
    }

    /**
     * Creates a new Die object with 6 sides and rolls it once upon initialization.
     */
    public Die() {
        this(6);
    }

    /**
     * Rolls the die and updates its value with a random number between 1 and 6.
     *
     * @return the new value of the die after rolling..
     */
    public int roll() {
        lastRolledValue = random.nextInt(numberOfSides) + 1;
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

    /**
     * Retrieves the number of sides of the die.
     *
     * @return the number of sides of the die.
     */
    public int getNumberOfSides() {
        return numberOfSides;
    }
}
