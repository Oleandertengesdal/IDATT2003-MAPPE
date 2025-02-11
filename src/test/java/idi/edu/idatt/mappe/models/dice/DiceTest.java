package idi.edu.idatt.mappe.models.dice;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiceTest {

    private Dice dice;

    @BeforeEach
    void setUp() {
        dice = new Dice(5); // Initialize with 5 dice
    }

    @Test
    void testRoll() {
        for (int i = 0; i < 100; i++) {
            int sum = dice.roll();
            assertTrue(sum >= 5 && sum <= 30, "The sume of two dice should be between 5 and 30");
        }
    }

    @Test
    void testGetValues() {
        dice.roll();
        List<Integer> values = dice.getValues();
        assertEquals(5, values.size(), "There should be two dice values");
        for (int value : values) {
            assertTrue(value >= 1 && value <= 6, "Each die value should be between 1 and 6");
        }
    }

    @Test
    void testGetDie() {
        //Check if all the dice are between 1 and 6.
        dice.roll();
        for (int i = 0; i < dice.getValues().size();i++) {
            int value = dice.getDie(i+1);
            assertTrue(value >= 1 && value <= 6, "Each die value should be between 1 and 6");
        }
    }

    @Test
    void testInvalidDiceCountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Dice(0), "Should throw exception for invalid dice count.");
    }

    @Test
    void testGetDieThrowsExceptionForInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> dice.getDie(0), "Die number must be between 1 and 5.");
        assertThrows(IllegalArgumentException.class, () -> dice.getDie(6), "Die number must be between 1 and 5.");
    }



}