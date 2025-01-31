package idi.edu.idatt.mappe.models.dice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DieTest {

    @Test
    void testRoll() {
        Die die = new Die();
        for (int i = 0; i < 100; i++) {
            int value = die.roll();
            assertTrue(value >= 1 && value <= 6, "Die value should be between 1 and 6");
        }
    }

    @Test
    void testGetValue() {
        Die die = new Die();
        die.roll();
        int value = die.getValue();
        assertTrue(value >= 1 && value <= 6, "Die value should be between 1 and 6");
    }
}