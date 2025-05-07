package idi.edu.idatt.mappe.models.dice;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static idi.edu.idatt.mappe.validators.DieValidator.validateNumberOfDice;
import static idi.edu.idatt.mappe.validators.DieValidator.validateGetDice;

/**
 * The Dice class represents a collection of dice that can be rolled together.
 * The number of dice is dynamically determined, making it flexible.
 * <p>
 *     The class uses java.util.ArrayList and
 *     java.util.List for storing the dice data.
 * </p>
 */
public class Dice {

    private static final Logger logger = Logger.getLogger(Dice.class.getName());

    private final List<Die> dice;

    /**
     * Creates a new object with a specified number of dice.
     *
     * @param numberOfDice the number of dice to include in this collection.
     * @throws IllegalArgumentException if numberOfDice is less than 1.
     */
    public Dice(int numberOfDice, int numberOfSides) {
        validateNumberOfDice(numberOfDice, numberOfSides);
        dice = new ArrayList<>();
        for (int i = 0; i < numberOfDice; i++) {
            dice.add(new Die(numberOfSides));
        }
    }

    /**
     * Creates a new object with a specified number of dice.
     *
     * @param numberOfDice the number of dice to include in this collection.
     * @throws IllegalArgumentException if numberOfDice is less than 1.
     */
    public Dice(int numberOfDice) {
        this(numberOfDice, 6);
    }

    /**
     * Rolls all dice in the collection and returns the sum of their values.
     *
     * @return the total sum of all rolled dice values
     */
    public int roll() {
        return dice.stream()
                .mapToInt(Die::roll)
                .sum();
    }

    /**
     * Retrieves the values of all dice after they have been rolled.
     *
     * @return a list of integers representing the values of each die
     */
    public List<Integer> getValues() {
        return List.copyOf(dice.stream()
                .map(Die::getValue)
                .toList());
    }

    /**
     * Retrieves the value of a specific die in the collection.
     *
     * @param dieNumber the position of the die in the list.
     * @return the value of the specified die.
     * @throws IllegalArgumentException if dieNumber is out of range.
     */
    public int getDie(int dieNumber) {
        if (dieNumber < 1 || dieNumber > dice.size()) {
            throw new IllegalArgumentException("Invalid dice index");
        }
        return dice.get(dieNumber - 1).getValue();
    }

    /**
     * Retrieves the number of dice in the collection.
     *
     * @return the number of dice in this collection.
     */
    public int getNumberOfDice() {
        return dice.size();
    }

    /**
     * Retrieves the number of sides on the dice.
     *
     * @return the number of sides on the dice.
     */
    public int getNumberOfSides(int index) {
        return dice.get(index).getNumberOfSides();
    }
}
