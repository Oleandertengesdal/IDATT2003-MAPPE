package idi.edu.idatt.mappe.models.dice;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Dice class represents a collection of dice that can be rolled together.
 * The number of dice is dynamically determined, making it flexible.
 */
public class Dice {
    private final List<Die> dice;

    /**
     * Creates a new object with a specified number of dice.
     *
     * @param numberOfDice the number of dice to include in this collection.
     * @throws IllegalArgumentException if numberOfDice is less than 1.
     */
    public Dice(int numberOfDice) {
        if (numberOfDice < 1) {
            throw new IllegalArgumentException("Number of dice must be at least 1.");
        }
        dice = new ArrayList<>();
        for (int i = 0; i < numberOfDice; i++) {
            dice.add(new Die());

        }
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
        return dice.stream()
                .map(Die::getValue)
                .collect(Collectors.toList());
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
            throw new IllegalArgumentException("Die number must be between 1 and " + dice.size() + ".");
        }
        return dice.get(dieNumber - 1).getValue();
    }
}
